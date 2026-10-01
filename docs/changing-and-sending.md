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

`replaceIn` keeps the message's original header id, swaps in your packet's body, and marks the packet edited, so G-Earth forwards the changed version. It throws an `IllegalArgumentException` if the message travels the other way from the packet, for example when you put a `Chat` into a message going to the client.

To drop a packet instead of changing it, block it through its `HMessage`; see [What the HMessage gives you](intercepting.md#what-the-hmessage-gives-you).

## Creating a packet

Build a packet with its constructor or its builder:

```java
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.outgoing.Chat;

Chat chat = new Chat("Hello, world!", ChatBarStyle.DEFAULT, -1);

// or
Chat chat = Chat.builder().text("Hello, world!").style(ChatBarStyle.DEFAULT).trackingId(-1).build();
```

## Sending a packet

Send an outgoing packet to the server with G-Earth's `sendToServer`:

```java
sendToServer(chat.toPacket());
```

Incoming packets go to the client with `sendToClient`. The client then acts as if the server had sent them. This one tells the client the catalog changed:

```java
import me.roboroads.gearth.gpackets.incoming.CatalogPublished;

CatalogPublished published = CatalogPublished.builder()
        .instantlyRefreshCatalogue(true)
        .build();

sendToClient(published.toPacket());
```

## Fields you leave empty

You don't have to set every field. A few rules decide what gets sent:

- Builder defaults apply. `Chat.builder().text("hi").build()` sends a `trackingId` of `-1`, because that's the field's default.
- A field left `null` is written as `0`, `""` or `false`.
- An object from a polymorphic family gets its type from its class. A `Player` built without a `type` is still written as a player.

## Editing without the typed class

If you're writing a generic tool, such as a packet editor, you may also read a packet as named values, change them and put them back. See [Changing values and putting them back](parameters.md#changing-values-and-putting-them-back).
