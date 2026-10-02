# Changing and sending packets

## Introduction

A parsed packet is a plain Java object with getters and setters. You may change an intercepted packet and put it back before G-Earth forwards it, or build a packet from scratch and send it to the client or the server.

## Changing an intercepted packet

Editing a parsed packet doesn't change the message on its own. Call `replaceIn(message)` to put your edit in:

```java
@Intercept(Chat.class)
void onChat(Chat chat, HMessage message) {
    chat.text(chat.text().toUpperCase());
    chat.replaceIn(message); // without this call, the original text goes through
}
```

The example uses annotations, but `replaceIn` works the same with whichever [intercepting option](intercepting.md#introduction) you picked: all it needs is the packet and the `HMessage`.

`replaceIn` keeps the message's original header id, swaps in your packet's body, and marks the packet edited, so G-Earth forwards the changed version. Bytes at the end that G-Packets doesn't know, such as a field a newer client added, stay in the message; see [Bytes the schema doesn't know](parameters.md#bytes-the-schema-doesnt-know). It throws an `IllegalArgumentException` if the message travels the other way from the packet, for example when you put a `Chat` into a message going to the client. For a packet going to the server, `replaceIn` also checks the client's [limits](#limits), like `toPacket()` does; `replaceInUnchecked(message)` skips that check.

To drop a packet instead of changing it, block it with G-Earth's `message.setBlocked(true)`.

## Creating a packet

Build a packet with its constructor or its builder:

```java
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.outgoing.Chat;

Chat chat = new Chat("Hello, world!", ChatBarStyle.DEFAULT, -1);

// or
Chat chat = Chat.builder().text("Hello, world!").style(ChatBarStyle.DEFAULT).trackingId(-1).build();
```

A chat style can be newer than your version of G-Packets: the hotel adds styles without a client update. `ChatBarStyle` names every style the library knows, and `ChatBarStyle.of(id)` takes any other id:

```java
ChatBarStyle style = ChatBarStyle.of(1028);

style.known(); // false: G-Packets has no name for it
style.value(); // 1028
```

A packet you read keeps such an id, and sends it back unchanged. Compare with `==` against a named style (`chat.style() == ChatBarStyle.ROBOT`), and with `equals` when both sides can be ids the library doesn't name. These types don't work in a `switch`. `ActivityPointType` works the same way.

## Sending a packet

Send an outgoing packet to the server with G-Earth's `sendToServer`:

```java
sendToServer(chat.toPacket());
```

Incoming packets go to the client with `sendToClient`. The client then acts as if the server had sent them. This one tells the client the catalog changed:

```java
import me.roboroads.gearth.gpackets.incoming.CatalogPublished;

CatalogPublished published = CatalogPublished.builder()
        .instantlyRefreshCatalog(true)
        .build();

sendToClient(published.toPacket());
```

## Limits

The client keeps some values within limits before it sends them: a room name is at most 60 characters, a room has at most two tags, an idle timeout is 30 to 3600 seconds. G-Packets knows these limits and checks them when you build an outgoing packet. If a value breaks one, `toPacket()` throws a `LimitException` that lists every broken limit:

```java
try {
    sendToServer(settings.toPacket());
} catch (LimitException e) {
    for (Violation violation : e.violations()) {
        System.out.println(violation.path() + ": " + violation.message());
        // name: at most 60 characters, got 61
    }
}
```

When you mean to send something the client wouldn't, for example to see how the server reacts, skip the check with `toPacketUnchecked()`:

```java
sendToServer(settings.toPacketUnchecked());
```

The same goes for an intercepted packet you change: `packet.replaceIn(message)` checks, and `packet.replaceInUnchecked(message)` doesn't. And for values: `TYPE.write(values)` and `TYPE.replaceIn(message, values)` check, and `TYPE.writeUnchecked(values)` and `TYPE.replaceInUnchecked(message, values)` don't. The exception's message names the call that skips the check. To check without sending or throwing, for example while someone fills in a form, call `TYPE.violations(packet)` or `TYPE.violations(values)`; it returns an empty list when everything fits.

Only packets you send to the server are checked. Packets from the server are never checked, so a value the server sends always parses and writes. The [packet reference](packets/index.md) lists each packet's limits.

## Fields you leave empty

You don't have to set every field. A few rules decide what gets sent:

- Builder defaults apply. `Chat.builder().text("hi").build()` sends a `trackingId` of `-1`, because that's the field's default.
- A field left `null` is written as `0`, `""` or `false`.
- An object from a polymorphic family gets its type from its class. A `Player` built without a `type` is still written as a player.

## Editing without the typed class

If you're writing a generic tool, such as a packet editor, you may also read a packet as named values, change them and put them back. See [Changing values and putting them back](parameters.md#changing-values-and-putting-them-back).
