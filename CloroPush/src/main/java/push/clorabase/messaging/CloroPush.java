package push.clorabase.messaging;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import port.org.json.JSONArray;
import port.org.json.JSONObject;


public class CloroPush {
    private static WebSocket socket;
    private static String clientId;
    private static Consumer<Message> listener;

    /**
     * Initialize the push messaging service.
     * @param clientId the client id used to identify a particular device or group of devices
     */
    public static void init(String clientId){
        CloroPush.clientId = clientId;
        try {
            socket = new WebSocketFactory()
                    .setConnectionTimeout(20000)
                    .createSocket("wss://clorabase.herokuapp.com/cloropush")
                    .addHeader("id",clientId)
                    .connectAsynchronously()
                    .addListener(new WebSocketAdapter(){
                        @Override
                        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                            new Thread(() -> new Timer().scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    System.out.println("Ping");
                                    socket.sendPing();
                                }
                            },10000,50000)).start();
                        }

                        @Override
                        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                            System.out.println("Disconnected");
                            System.out.println(serverCloseFrame.getCloseReason());
                            System.out.println(clientCloseFrame.getCloseReason());
                        }

                        @Override
                        public void onTextMessage(WebSocket websocket, String text) throws Exception {
                            var map = new JSONObject(text).toMap();
                            var from = (String) map.get("from");
                            map.remove(from);
                            map.remove("to");
                            if (listener != null)
                                listener.accept(new Message(from,map));
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attach a listener for listening realtime push notification.
     * @param consumer the consumer which will have the message.
     */
    public static void attachOnPushMessageListener(Consumer<Message> consumer){
        listener = consumer;
    }

    /**
     * Gets the unread push messages that were sent when this client was offline.
     * @return Array of {@link Message}. May be empty but never null
     * @throws IOException If any IO error occurred while communicating with the server.
     */
    public static Future<Message[]> getPendingPushMessages() throws IOException, InterruptedException {
        return Executors.newCachedThreadPool().submit(() -> {
            var documents = getDocuments(clientId);
            var messages = new Message[documents.length()];
            for (int i = 0; i < documents.length(); i++) {
                var message = documents.getJSONObject(i);
                messages[i] = new Message(message.getString("from"),message.toMap());
            }
            if (messages.length > 0)
                deleteMessages(clientId);
            return messages;
        });
    }

    /**
     * Sends a push message to the target client
     * @param to The clientId of the receiver
     * @param message The message to sent
     */
    public static void sendPushMessage(String to,Message message) {
        var payload = new JSONObject(message.payload());
        payload.put("to",to);
        socket.sendText(payload.toString());
    }

    private static void deleteMessages(String collection) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://clorabase.herokuapp.com/clorastore/CloroPushDatabase?path=clients/" + collection)
                .delete(null)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Basic GITHUB_PERSONAL_TOKEN)
                .build();
        client.newCall(request).execute();
    }

    private static JSONArray getDocuments(String collection) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://clorabase.herokuapp.com/clorastore/CloroPushDatabase?path=clients/" + collection)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Basic GITHUB_PERSONAL_TOKEN")
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful())
            return new JSONArray(response.body().string());
        else
            return new JSONArray();
    }
}
