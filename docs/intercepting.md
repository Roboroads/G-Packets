# Intercepting packets

## Introduction

Intercepting means G-Earth hands your extension every packet of a certain type as it passes between the client and the server. G-Packets turns that packet into a typed object, so you work with `users.users()` instead of reading bytes.

There are three ways to intercept. They are alternatives: pick the one you like and use it for your whole extension. You don't need the others.

| Option | What you write | Pick it when |
|---|---|---|
| [Option 1: annotations](#option-1-annotations-recommended) (recommended) | `@Intercept` methods and one `GPackets.init(this)` | You're starting out, or you want the least code |
| [Option 2: the TYPE descriptor](#option-2-the-type-descriptor) | a lambda per packet type | You prefer lambdas, or you add and remove handlers at runtime |
| [Option 3: raw interception](#option-3-raw-interception) | G-Earth's own `intercept`, with `fromPacket` inside | You already have raw G-Earth listeners and only want the parsing |

All three use the same packet classes and give you the same `HMessage`, so [changing](changing-and-sending.md) and blocking work the same way in each.

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

### Blocking a packet

Take the `HMessage` and block it to stop the packet from reaching the other side:

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

Handlers don't have to live in your extension class. Pass any other objects to `init` and their `@Intercept` methods are registered too:

```java
public class ChatLogger {
    @Intercept
    void onChat(Chat chat) {
        System.out.println("You said: " + chat.text());
    }
}
```

```java
@Override
protected void initExtension() {
    GPackets.init(this, new ChatLogger());
}
```

### When init fails

`init` checks every annotated method before it registers anything. If one is static, returns a value, takes parameters it can't fill, or names a packet class without a `TYPE`, it throws an `IllegalStateException` that names the method and the problem, and registers nothing.

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

### Blocking a packet

The handler gets the `HMessage` as its second argument:

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

### Blocking a packet

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
