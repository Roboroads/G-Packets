# Contributing a packet

## Introduction

G-Packets doesn't implement every Habbo packet yet, and adding one is usually a small pull request. A packet is a Java class plus a schema: the list of its parameters in the order they appear on the wire. The schema does the reading and writing, so you never call `readInteger()` or `appendString()` yourself.

To find a packet's layout, look it up on [Sulek](https://sulek.dev) or in the decompiled client. If you'd rather not write it yourself, [open a packet request issue](https://github.com/Roboroads/G-Packets/issues/new/choose).

## A packet class

Here is the whole `Chat` packet, the message you send when you talk in a room:

```java
package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Chat implements Packet, JsonSerializable {
    public static final PacketType<Chat> TYPE = PacketType.of("Chat", HMessage.Direction.TOSERVER, Schema.of(Chat.class)
            .string("text")
            .enumInt("style", ChatBarStyle.class)
            .integer("trackingId"));

    private String text;
    private ChatBarStyle style;
    @Builder.Default
    private int trackingId = -1;

    public static Chat fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Chat fromJson(String json) {
        return Json.parse(Chat.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
```

A packet class needs four things:

- `TYPE`, built with `PacketType.of(header, direction, schema)`. The header is the packet's name on Sulek. The direction is `TOCLIENT` for incoming packets and `TOSERVER` for outgoing ones.
- A static `fromPacket` that returns `TYPE.schema().parse(packet)`.
- A static `fromJson`.
- `toPacket`, which returns `TYPE.toPacket(this)`.

The parameter names must match the field names: G-Packets fills the fields through the Lombok builder and reads them back through the getters. Incoming packets go in the `incoming` package and outgoing ones in `outgoing`.

## Describing the parameters

Each method adds one parameter to the schema:

| Method | On the wire | Field type |
|---|---|---|
| `integer(name)` | int | `Integer` or `int` |
| `string(name)` | string | `String` |
| `bool(name)` | boolean | `Boolean` or `boolean` |
| `shortValue(name)` | short | `Short` or `short` |
| `longValue(name)` | long | `Long` or `long` |
| `byteValue(name)` | byte | `Byte` or `byte` |
| `enumInt(name, E.class)` | int | an enum that implements `IntEnum` |
| `enumString(name, E.class)` | string | an enum that implements `StringEnum` |

Add them in wire order, which isn't always the order of the fields. A furni product, for example, sends its `extraParam` after its `furniClassId`, even though `extraParam` is declared on the parent class. Every method returns a new schema, so a finished schema can't change.

## Lists

A list is an int count followed by that many elements. Use `list(name, WireType.INT)` for a list of values and `list(name, Offer.SCHEMA)` for a list of structures. When a structure contains a list of itself, pass a lambda, so the schema is looked up once it exists:

```java
public static final Schema<CatalogNode> SCHEMA = Schema.of(CatalogNode.class)
        .bool("visible")
        .integer("icon")
        .integer("pageId")
        .string("pageName")
        .string("localization")
        .list("offerIds", WireType.INT)
        .list("children", () -> CatalogNode.SCHEMA);
```

!!! note
    Write `CatalogNode.SCHEMA`, not `SCHEMA`, inside the lambda. Java doesn't let a field refer to itself by its simple name in its own initializer.

## Nested structures

A structure that only ever appears inside a packet, like a catalog page's localization, is a sub-packet. It implements `SubPacket`, exposes its own `SCHEMA`, and delegates to it:

```java
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Localization implements SubPacket, JsonSerializable {
    public static final Schema<Localization> SCHEMA = Schema.of(Localization.class)
            .list("images", WireType.STRING)
            .list("texts", WireType.STRING);

    private List<String> images;
    private List<String> texts;

    public static Localization fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
```

Use it in a packet with `struct("localization", Localization.SCHEMA)`, or in a list.

## Polymorphic packets

Some structures change shape depending on a value: a user is a player, a pet or a bot depending on its `type`. Describe that with `branch`. The base class holds the schema for every case, and each case names the wire value, the subclass, and the parameters that follow:

```java
public static final Schema<User> SCHEMA = Schema.of(User.class)
        .integer("id")
        .string("name")
        // ...
        .enumInt("type", UserType.class)
        .branch("type", cases -> cases
                .on(UserType.PLAYER, Player.class, s -> s
                        .enumString("sex", Gender.class)
                        .integer("groupId")
                        // ...
                        .bool("isModerator"))
                .on(UserType.OLD_BOT, OldBot.class, s -> s)
                // ...
        );
```

The subclasses keep only their fields, constructors and `fromJson`; they don't override `appendPacket`. When several values share one shape, keep the body in a variable and reuse it:

```java
UnaryOperator<Schema<FurniProduct>> furni = s -> s
        .integer("furniClassId")
        .string("extraParam")
        // ...
        ;
SCHEMA = Schema.of(Product.class)
        .enumString("productType", ProductType.class)
        .branch("productType", cases -> cases
                .on(ProductType.BADGE, BadgeProduct.class, s -> s.string("extraParam"))
                .on(ProductType.ITEM, FurniProduct.class, furni)
                .on(ProductType.STUFF, FurniProduct.class, furni)
                // ...
        );
```

## Conditional parameters

Use `when` for parameters that are only sent when an earlier value says so. A wired user movement only sends `jumpPower` when `hasJump` is true:

```java
.bool("hasJump")
.when("hasJump", true, j -> j.integer("jumpPower"))
```

## Optional trailing parameters

Some servers leave parameters off the end of a packet. Wrap those in `optional`: they're read only when bytes are left, and written only when one of them is set.

```java
public static final PacketType<CatalogPublished> TYPE = PacketType.of("CatalogPublished", HMessage.Direction.TOCLIENT, Schema.of(CatalogPublished.class)
        .bool("instantlyRefreshCatalogue")
        .optional(s -> s.string("newFurniDataHash")));
```

## Enums

An enum parameter needs an enum that implements `IntEnum` or `StringEnum`. Lombok's `@Getter` on the `value` or `code` field already provides the method:

```java
@RequiredArgsConstructor
public enum Direction implements IntEnum {
    NORTH(0),
    NORTH_EAST(1),
    // ...
    NORTH_WEST(7);

    @Getter
    @JsonValue
    private final int value;
```

If you can't name every value the client knows, keep the field a plain `Integer`, use `integer(...)`, and list the values you do know in a comment. An enum turns a value it doesn't have into `null`, so that value would be lost when you write the packet again.

## Registering the packet

Add the new `TYPE` to the list in `PacketTypes`:

```java
private static final List<PacketType<?>> ALL = Collections.unmodifiableList(Arrays.<PacketType<?>>asList(
        CatalogIndex.TYPE,
        // ...
        YourPacket.TYPE
));
```

That puts the packet in `PacketTypes.find` and in the generated [packet reference](packets/index.md).

## Testing

Start with a test that pins the exact bytes, so you know the schema matches the wire format:

```java
class ChatWireFormatTest {

    private static Chat sample() {
        return new Chat("hello", ChatBarStyle.ROBOT, 5);
    }

    private static HPacket expectedPacket() {
        HPacket p = new HPacket("Chat", HMessage.Direction.TOSERVER);
        p.appendString("hello").appendInt(2).appendInt(5);
        return p;
    }

    @Test
    void toPacketWritesTheWireFormat() {
        assertSameBytes(expectedPacket(), sample().toPacket());
    }

    @Test
    void fromPacketReadsTheWireFormat() {
        assertEquals(sample(), Chat.fromPacket(expectedPacket()));
    }
}
```

`assertSameBytes` comes from `WireAssert`, next to the other tests in `src/test/java/me/roboroads/gearth/gpackets`. `PacketImplementationTest` checks the rest for you: that the class has `fromPacket`, `fromJson` and `TYPE`, that it's in `PacketTypes`, and that every parameter has a getter and a builder method that takes the type the schema reads. Run everything with:

```bash
mvn test
```

## Opening a pull request

Pull request titles follow Conventional Commits, for example `feat: add the Whisper packet`. If your change breaks existing code, such as a renamed field, add a `!`: `feat!: rename Chat.text to Chat.message`. Pull requests are squash-merged, so the title becomes the commit that decides the next version number.
