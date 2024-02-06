## ⚠️DEPRECATED : NO LONGER WORKING
The push messaging service was hosted on the Heroku free tier, since Heroku has ended support for free usage, all the APIs and services running on that are shutdown permanently.

# CloroPush ~ a simple messaging protocol

Cloropush messaging protocol is designed to be simple for simple uses. This can be used in chating app, when there is a new message or notification. In games, for any realtime event or in any application which requires to deliver realtime & offline messages to the client. The best thing is that you don't need a **server for this** to setup.

## Features
- Easy, Lightweight and simple
- No server needed
- Support's both realtime and offtime (offline)

## Implimentation
#### Gradle
In your project *build.gradle*,
```css
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
In your module *build.gradle*,
```css
dependencies {
	 implementation 'com.github.ErrorxCode:CloroPush:1.0.0'
}
```
#### Maven
```markup
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
Add the dependency,
```markup
	<dependency>
	    <groupId>com.github.ErrorxCode</groupId>
	    <artifactId>CloroPush</artifactId>
	    <version>1.0.0</version>
	</dependency>
```

## Documentation
First of all, you have to initialize the library providing a unique id that identifies the client.
```java
CloroPush.init("testClientId");
```

### Starting realtime messaging
Then you can attach the listener for realtime messages to it,
```java
CloroPush.attachOnPushMessageListener(message -> {  
    String sender = message.from();
    Map<String,Object> data = message.payload();  
});
```
This will enable realtime messaging on the client.


###  Fetching pending messages
If a push notification is sent to your **ID** when the client was offline, those messages are saved to pending push messages database. You can retrive them like this,
```java
try {  
    var messages = CloroPush.getPendingPushMessages().get(); 
    // process the messages, it get deleted after this. 
} catch (ExecutionException | InterruptedException | IOException e) {  
    e.printStackTrace();  
}
```

### Sending push message
To send push message to a client, you must know his/her *clientId*.
```java
var data = new HashMap<String,Object>();  
data.put("name","Jhon");  
data.put("type","payload");  
.... ...
.... ...
// As many data as you want

var message = new Message(null,data);  
CloroPush.sendPushMessage("receiverId",message); // Fire it!
```
**Note:** That first argument of the `Message` constructor is never used, So it dosn't matter what you passed. It will always consider your **clientId** as the sender addrress.


## How it's work ?
CloroPush messaging uses websockets which further uses **Transmission Control Protocol**. To send & recieve messages.  Each and every client is registered to a *client id* during initializing the class. This **id** is used by the sender to sent message to this client.
If the device is online, the message is directly delivered to the client at realtime, or if not, the message are saved in the ***pending queue in the our server database***. When client come online, it can retrive those pending messages and can process them.


## That's it
That's what you need to know all about the **CloroPush** messaging. If you like this project, Please support us by *staring this repo* for active developement.

***All we wan't is a ⭐ from your side***
