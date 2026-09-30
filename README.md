# G-Packets

G-Packets parses, builds and serializes Habbo packets for G-Earth extensions, so you don't have to reverse-engineer the byte layout yourself. The server sends raw data with no context; each packet here is a Java class that reads and writes that data through named fields.

See [Sulek](https://sulek.dev) for an overview of packets. This is a work in progress, so not every packet is implemented yet. If the one you need is missing, open a pull request.

## Installation

G-Packets is published through JitPack. It needs Java 8 or later and a G-Earth extension to run inside.

Maven:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.Roboroads</groupId>
        <artifactId>G-Packets</artifactId>
        <version>main-SNAPSHOT</version>
    </dependency>
</dependencies>
```

Gradle:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Roboroads:G-Packets:main-SNAPSHOT'
}
```

## Quick start

Call `GPackets.init(this)` once from your extension, then annotate a method with `@Intercept`. Init scans the extension for annotated methods and registers each one through G-Earth's own `intercept(...)`. If you never call init, nothing is scanned.

```java
import gearth.extensions.ExtensionForm;
import gearth.protocol.HMessage;
import me.roboroads.gearth.gpackets.GPackets;
import me.roboroads.gearth.gpackets.Intercept;
import me.roboroads.gearth.gpackets.incoming.Users;

public class YourExtension extends ExtensionForm {
    @Override
    protected void initExtension() {
        GPackets.init(this);
    }

    @Intercept(Users.class)
    void onUsers(Users users) {
        System.out.println("There are " + users.users().size() + " users in this room.");
    }
}
```

An `@Intercept` method returns `void`, is not static, and takes the packet first. The `HMessage` is optional, so three shapes are valid:

```java
@Intercept(Users.class)
void onUsers(Users users) { }

@Intercept(Users.class)
void onUsers(Users users, HMessage message) { }

@Intercept(Users.class)
void onUsers(HMessage message) { } // no parsing
```

Block a packet with the message:

```java
@Intercept(Chat.class)
void onChat(Chat chat, HMessage message) {
    if (chat.text().contains("spoiler")) {
        message.setBlocked(true);
    }
}
```

One method can handle several packet types. List them all, and take the packet as `Packet`:

```java
@Intercept({Users.class, Chat.class})
void onEither(Packet packet, HMessage message) {
    if (packet instanceof Chat) {
        // ...
    }
}
```

Init fails fast. If an annotated method is static, returns a value, has parameters it cannot accept, or names a packet class without a `TYPE`, `init` throws `IllegalStateException` naming the method and the problem, and registers nothing. Calling init twice registers the handlers twice.

## Changing an intercepted packet

Editing a parsed packet does not change the message on its own. To send your edit, call `replaceIn(message)`:

```java
@Intercept(Chat.class)
void onChat(Chat chat, HMessage message) {
    chat.text(chat.text().toUpperCase());
    chat.replaceIn(message); // without this call, the original text goes through
}
```

`replaceIn` keeps the message's original header, swaps in your packet's body, and marks the packet edited so G-Earth forwards the changed version. It throws `IllegalArgumentException` if the message direction does not match the packet's direction.

## Without reflection: TYPE

Every packet class exposes a `PacketType` as `public static final PacketType<X> TYPE`. It holds the header, direction and parser, and it is the single source of truth for that packet.

`TYPE.intercept` registers a handler without annotations or `init`:

```java
@Override
protected void initExtension() {
    Users.TYPE.intercept(this, (users, message) -> {
        System.out.println(users.users().size() + " users");
    });
}
```

`TYPE.listen` wraps a handler as a G-Earth listener if you want to register it yourself:

```java
intercept(Users.TYPE.direction(), Users.TYPE.header(), Users.TYPE.listen((users, message) -> {
    // ...
}));
```

`replaceIn` works here too.

## Raw

At the lowest level, register with the header and direction from `TYPE` and parse the packet yourself with `fromPacket`:

```java
intercept(Users.TYPE.direction(), Users.TYPE.header(), message -> {
    Users users = Users.fromPacket(message.getPacket());
    // ...
});
```

`fromPacket` reads from the packet's current read index, so parse before anything else reads it.

## Creating and sending packets

Build a packet with its constructor or its builder, then send it with G-Earth's `sendToServer` or `sendToClient`:

```java
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.outgoing.Chat;

Chat chat = new Chat("Hello, world!", ChatBarStyle.DEFAULT, -1);
// or
Chat chat = Chat.builder().text("Hello, world!").style(ChatBarStyle.DEFAULT).trackingId(-1).build();

sendToServer(chat.toPacket());
```

## JSON

Packets serialize to and from JSON with `toJson` and `fromJson`:

```java
Chat chat = new Chat("Hello, world!", ChatBarStyle.DEFAULT, -1);
String json = chat.toJson();
Chat again = Chat.fromJson(json);
sendToServer(again.toPacket());
```

## Contributing a packet

A packet class needs four things:

- `public static final PacketType<X> TYPE` with the header, direction and `X::fromPacket`
- a static `fromPacket(HPacket)`
- a static `fromJson(String)`
- an instance `toPacket()`

`PacketImplementationTest` checks every `Packet` implementation for these and fails the build if one is missing. Follow the existing classes in `incoming` and `outgoing`, and open a pull request or file a packet request issue.
