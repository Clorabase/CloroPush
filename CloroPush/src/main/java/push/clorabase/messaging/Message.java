package push.clorabase.messaging;

import java.util.Map;
import java.util.Objects;

import port.org.json.JSONObject;

public final class Message {
    private final String from;
    private final Map<String, Object> payload;

    public Message(String from, Map<String, Object> payload) {
        this.from = from;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from='" + from + '\'' +
                ", payload=" + new JSONObject(payload) +
                '}';
    }

    public String from() {
        return from;
    }

    public Map<String, Object> payload() {
        return payload;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Message) obj;
        return Objects.equals(this.from, that.from) &&
                Objects.equals(this.payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, payload);
    }

}
