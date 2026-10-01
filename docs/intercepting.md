# Intercepting packets

## Introduction

Intercepting means G-Earth hands your extension every packet of a certain type as it passes between the client and the server. G-Packets turns that packet into a typed object, so you work with `users.users()` instead of reading bytes.

There are three ways to intercept, and they all use the same packet classes: annotations, the `TYPE` descriptor, and raw interception. Annotations are the shortest, so start there unless you have a reason not to.

## Intercepting with annotations

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

A handler returns `void`, isn't static, and takes the packet first. The G-Earth `HMessage` is optional, which gives three shapes:

```java
@Intercept
void onUsers(Users users) { }

@Intercept
void onUsers(Users users, HMessage message) { }

@Intercept(Users.class)
void onUsers(HMessage message) { } // no packet parameter, so the type is listed
```

## Blocking a packet

Take the `HMessage` and block it to stop the packet from reaching the other side:

```java
@Intercept(Chat.class)
void onChat(Chat chat, HMessage message) {
    if (chat.text().contains("spoiler")) {
        message.setBlocked(true);
    }
}
```

## Intercepting several packets in one method

List the packet classes on the annotation and take the packet as `Packet`:

```java
@Intercept({Users.class, Chat.class})
void onEither(Packet packet, HMessage message) {
    if (packet instanceof Chat) {
        // ...
    }
}
```

## Handlers in other classes

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

## When init fails

`init` checks every annotated method before it registers anything. If one is static, returns a value, takes parameters it can't fill, or names a packet class without a `TYPE`, it throws an `IllegalStateException` naming the method (`YourExtension#onUsers`) and the problem, and registers nothing.

!!! warning
    Calling `init` twice registers every handler twice, so each packet reaches your methods twice. Call it once, from `initExtension`.

## Using the TYPE descriptor

Every packet class has a `TYPE` that knows its header, direction and parameters. If you'd rather not use annotations, `TYPE.intercept` registers a handler directly:

```java
@Override
protected void initExtension() {
    Users.TYPE.intercept(this, (users, message) -> {
        System.out.println(users.users().size() + " users");
    });
}
```

`TYPE.listen` gives you a plain G-Earth listener, for when you want to register it yourself:

```java
intercept(Users.TYPE.direction(), Users.TYPE.header(), Users.TYPE.listen((users, message) -> {
    // ...
}));
```

## Raw interception

At the lowest level, register with G-Earth using the header and direction from `TYPE`, and parse the packet yourself with `fromPacket`:

```java
intercept(Users.TYPE.direction(), Users.TYPE.header(), message -> {
    Users users = Users.fromPacket(message.getPacket());
    // ...
});
```

!!! note "Read index"
    `@Intercept`, `TYPE.intercept`, `TYPE.parse` and `TYPE.read` read from the start of the packet and put the read index back afterwards, so several handlers can read the same packet. `fromPacket` reads from the current read index instead, so call it before anything else reads the packet.

Once you have a packet, [Changing and sending packets](changing-and-sending.md) shows how to edit it or send your own. To handle packets without their typed classes, see [Packet parameters](parameters.md).
