# G-Packets

A library for your G-Earth extensions, reverse-engineered for your convenience.

## Installation

You can install this through JitPack by adding the repository and dependency to your `pom.xml`:

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
<dependency>
    <groupId>me.roboroads.gearth</groupId>
    <artifactId>G-Packets</artifactId>
    <version>main-SNAPSHOP</version>
</dependency>
</dependencies>
```

## Usage

Check [Sulek](https://sulek.dev) for an overview of packets. Since this is a work in progress, not all packets are implemented yet.

> Is your packet not implemented yet? Please open a pull request with said packet implemented!

### Parsing intercepted packets

Packets that are intercepted can be parsed using the `fromPacket` method on the packet class with the same name as the header of the packet. For example, if you want to parse the `Users` packet, you can do it like this:

```java
import me.roboroads.gearth.gpackets.incoming.Users;

import static gearth.protocol.HMessage.Direction;

public class YourExtension extends ExtensionForm {
    @Override
    protected void initExtension() {
        intercept(Direction.TOCLIENT, Users.HEADER, (HMessage hMessage) -> {
            Users users = Users.fromPacket(hMessage.getPacket());
            System.out.println("There are " + users.users().size() + " users in this room.");
            System.out.println("The first user is called " + users.users().getFirst().name() + "!");
        });
    }
}
```

### Modifying packets

You're allowed to modify the parsed packets. All data has been parsed and can be accessed by getters and setters. For example, if you want to change the name of the first user in the `Users` packet and then send it to the client, you can do it like this:

```java
import me.roboroads.gearth.gpackets.incoming.Users;

import static gearth.protocol.HMessage.Direction;

public class YourExtension extends ExtensionForm {
    @Override
    protected void initExtension() {
        intercept(Direction.TOCLIENT, Users.HEADER, (HMessage hMessage) -> {
            Users users = Users.fromPacket(hMessage.getPacket());
            hMessage.setBlocked(true); // Block the original packet from being sent
            users.users().getFirst().name("NewName"); // Change the name of the first user
            sendToClient(users.toPacket()); // Send the modified packet to the client
        });
    }
}
```

### Creating packets

Packets can be created using their constructor or using a packet builder. For example, if you want to create a `Chat` packet and send it to the server, you can do it like this:

```java
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.outgoing.Chat;

import static gearth.protocol.HMessage.Direction;

public class YourExtension extends ExtensionForm {
    @Override
    protected void initExtension() {
        // Class initialization
        Chat chat = new Chat("Hello, world!", ChatBarStyle.DEFAULT, -1);
        // or use the builder
        Chat chat = Chat.builder().text("Hello, world!").style(ChatBarStyle.DEFAULT).trackingId(-1).build();

        sendToServer(chat.toPacket());
    }
}
```

### JSON Serialization

Packets can be serialized to and from JSON using the `toJson` and `fromJson` methods. For example, if you want to serialize a `Chat` packet to JSON and then deserialize it back to a packet, you can do it like this:

```java
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.outgoing.Chat;

public class YourExtension extends ExtensionForm {
    @Override
    protected void initExtension() {
        Chat chat = new Chat("Hello, world!", ChatBarStyle.DEFAULT, -1);
        String json = chat.toJson();
        Chat deserializedChat = Chat.fromJson(json);
        sendToServer(deserializedChat.toPacket());
    }
}
```