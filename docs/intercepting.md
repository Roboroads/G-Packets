# Intercepting packets

## Introduction

Intercepting means G-Earth hands your extension every packet of a certain type as it passes between the client and the server. G-Packets turns that packet into a typed object, so you work with `users.users()` instead of reading bytes.

There are three ways to intercept. They are alternatives: pick the one you like and use it for your whole extension. You don't need the others.

| Option | What you write | Pick it when |
|---|---|---|
| [Option 1: annotations](#option-1-annotations-recommended) (recommended) | `@Intercept` methods and one `GPackets.init(this)` | You're starting out, or you want the least code |
| [Option 2: the TYPE descriptor](#option-2-the-type-descriptor) | a lambda per packet type | You prefer lambdas, or you add and remove handlers at runtime |
| [Option 3: raw interception](#option-3-raw-interception) | G-Earth's own `intercept`, with `fromPacket` inside | You already have raw G-Earth listeners and only want the parsing |

All three use the same packet classes and give you the same G-Earth `HMessage`, so [changing a packet](changing-and-sending.md) and working with the `HMessage` work the same way in each.

## Option 1: annotations (recommended)

### Intercepting a packet

Call `GPackets.init(this)` once from your extension, then put `@Intercept` on any method that takes a packet:

```java
import gearth.extensions.ExtensionForm;
import me.roboroads.gearth.gpackets.GPackets;
import me.roboroads.gearth.gpackets.Intercept;
import me.roboroads.gearth.gpackets.incoming.Users;

public class YourExtension extends ExtensionForm {
    @Override
    protected void initExtension() {
        GPackets.init(this);
    }

    @Intercept
    void onUsers(Users users) {
        System.out.println("There are " + users.users().size() + " users in this room.");
    }
}
```

`init` scans your extension for `@Intercept` methods and registers each one with G-Earth. With no value, `@Intercept` takes the packet type from the method's first parameter, so `onUsers(Users users)` handles `Users`. If you never call `init`, nothing is scanned.

### Method shapes

A handler returns `void`, isn't static, and takes the packet first. The G-Earth `HMessage` is optional, which gives three shapes:

```java
@Intercept
void onUsers(Users users) { }

@Intercept
void onUsers(Users users, HMessage message) { }

@Intercept(Users.class)
void onUsers(HMessage message) { } // no packet parameter, so the type is listed
```

### Working with the HMessage

Add an `HMessage` parameter after the packet to get G-Earth's message around it, and use it as you would in any G-Earth extension, for example to block the packet. This handler blocks chat messages that mention a spoiler:

```java
@Intercept(Chat.class)
void onChat(Chat chat, HMessage message) {
    if (chat.text().contains("spoiler")) {
        message.setBlocked(true);
    }
}
```

### Several packets in one method

List the packet classes on the annotation and take the packet as `Packet`:

```java
import me.roboroads.gearth.gpackets.support.Packet;

@Intercept({Users.class, Chat.class})
void onEither(Packet packet, HMessage message) {
    if (packet instanceof Chat) {
        // ...
    }
}
```

### Handlers in other classes

Handlers don't have to live in your extension class. Put `@Intercept` methods in any class in your extension's package, or a package below it, and `GPackets.init(this)` finds the class, creates it and registers its methods. There's nothing to list:

```java
public class ChatLogger {
    @Intercept
    void onChat(Chat chat) {
        System.out.println("You said: " + chat.text());
    }
}
```

To create a handler, `init` uses a constructor that takes your extension, so the handler can send packets, or else a constructor without parameters:

```java
public class AutoReply {
    private final YourExtension extension;

    public AutoReply(YourExtension extension) {
        this.extension = extension;
    }

    @Intercept
    void onChat(Chat chat) {
        if (chat.text().equals("ping")) {
            extension.sendToServer(new Chat("pong", ChatBarStyle.DEFAULT, -1).toPacket());
        }
    }
}
```

The rest of your code can't reach the instances `init` creates. If a handler needs anything else in its constructor, or other code needs the same instance (a singleton, or state your GUI shows), create it yourself and pass it to `init`. A class you pass an instance of isn't created a second time:

```java
GPackets.init(this, new ChatLogger(database));
```

When several handlers take the same packet, G-Earth calls them in no particular order, and each one still gets the packet after another one blocked it. Check `message.isBlocked()` if a handler should leave blocked packets alone.

### When init fails

`init` checks everything before it registers anything. If an annotated method is static, returns a value, takes parameters it can't fill, or names a packet class without a `TYPE`, or if a handler class it found has no constructor it can use or its constructor throws, `init` throws an `IllegalStateException` that names the method or class and the problem, and registers nothing.

!!! warning
    Calling `init` twice registers every handler twice, so each packet reaches your methods twice. Call it once, from `initExtension`.

## Option 2: the TYPE descriptor

### Intercepting a packet

Every packet class has a `TYPE` that knows its header, direction and parameters. `TYPE.intercept` registers a handler directly, without annotations or `init`:

```java
@Override
protected void initExtension() {
    Users.TYPE.intercept(this, (users, message) -> {
        System.out.println(users.users().size() + " users");
    });
}
```

### Working with the HMessage

The handler gets G-Earth's `HMessage` as its second argument. Use it as you would in any G-Earth extension, for example to block the packet. This handler blocks chat messages that mention a spoiler:

```java
Chat.TYPE.intercept(this, (chat, message) -> {
    if (chat.text().contains("spoiler")) {
        message.setBlocked(true);
    }
});
```

### Registering the listener yourself

`TYPE.listen` gives you a plain G-Earth listener that parses the packet first, for when you want to call G-Earth's `intercept` yourself:

```java
intercept(Users.TYPE.direction(), Users.TYPE.header(), Users.TYPE.listen((users, message) -> {
    // ...
}));
```

## Option 3: raw interception

### Intercepting a packet

Register with G-Earth using the header and direction from `TYPE`, and parse the packet yourself with `fromPacket`:

```java
intercept(Users.TYPE.direction(), Users.TYPE.header(), message -> {
    Users users = Users.fromPacket(message.getPacket());
    // ...
});
```

### Working with the HMessage

G-Earth's listener already hands you the `HMessage`; the typed packet is something you parse from it. Use the message as you would in any G-Earth extension, for example to block the packet. This listener blocks chat messages that mention a spoiler:

```java
intercept(Chat.TYPE.direction(), Chat.TYPE.header(), message -> {
    Chat chat = Chat.fromPacket(message.getPacket());
    if (chat.text().contains("spoiler")) {
        message.setBlocked(true);
    }
});
```

!!! note "Read index"
    `fromPacket` reads from the packet's current read index, so call it before anything else reads the packet. Options 1 and 2 don't have this catch: they read from the start of the packet and put the read index back afterwards, so several handlers can read the same packet.

Once you have a packet, [Changing and sending packets](changing-and-sending.md) shows how to edit it or send your own. To handle packets without their typed classes, see [Packet parameters](parameters.md).
