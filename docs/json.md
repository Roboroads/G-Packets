# JSON

## Introduction

Every packet and sub-packet converts to and from JSON. That's handy when you want to log packets, store them, or hand them to another process.

## Converting a packet to JSON

Call `toJson` on any packet:

```java
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.outgoing.Chat;

Chat chat = Chat.builder().text("hi").style(ChatBarStyle.ROBOT).trackingId(3).build();

String json = chat.toJson();
// {"text":"hi","style":2,"trackingId":3}
```

Enums are written as their wire value, so `ROBOT` becomes `2`.

## Reading a packet from JSON

Each packet class has a static `fromJson`:

```java
Chat again = Chat.fromJson(json);

sendToServer(again.toPacket());
```

## Polymorphic packets

Some packets hold objects of different subclasses. A `Users` packet, for example, holds `Player`, `Pet`, `OldBot` and `Bot` objects. Their JSON keeps the `type` field (`1` to `4`), and `Users.fromJson` uses it to build the right subclass again:

```java
import me.roboroads.gearth.gpackets.incoming.Users;
import me.roboroads.gearth.gpackets.incoming.sub.user.Player;
import me.roboroads.gearth.gpackets.incoming.sub.user.User;

Users users = Users.fromJson(json);

for (User user : users.users()) {
    if (user instanceof Player) {
        System.out.println(user.name() + " is a player");
    }
}
```

Wired movements (`movementType`), catalog products (`productType`) and front page items (`type`) work the same way.

!!! note
    JSON comes from the class's fields, so it always has every field, with `null` for the ones that aren't set. The values from `TYPE.read` come from the wire instead and leave out what wasn't sent. See [Packet parameters](parameters.md) if you need the wire view.
