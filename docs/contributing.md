# Contributing a packet

## Introduction

G-Packets doesn't implement every packet yet, and adding one is usually a small pull request. A packet is a Java class plus a schema: the list of its parameters in the order they appear on the wire. The schema does the reading and writing, so you never call `readInteger()` or `appendString()` yourself.

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
| `floatValue(name)` | float | `Float` or `float` |
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
        .string("pageTitle")
        .list("offerIds", WireType.INT)
        .list("children", () -> CatalogNode.SCHEMA);
```

!!! note
    Write `CatalogNode.SCHEMA`, not `SCHEMA`, inside the lambda. Java doesn't let a field refer to itself by its simple name in its own initializer.

A few packets count a list with a byte or a short instead of an int. The client reads a height map update's count with `readByte()`, so its schema names the count type:

```java
.listWithCount("tileUpdates", WireType.BYTE, HeightMapTileUpdate.SCHEMA)
```

Writing a list longer than its count can hold (127 for a byte) throws an `IllegalArgumentException`.

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
                        .bool("isModerator")
                        .integer("badgesRank"))
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

When several values share the parameters that follow, list them with `whenOneOf`. A roller only sends the user it moves along when the move type is walk or slide, and the user looks the same for both:

```java
.enumInt("userMoveType", SlideUserMoveType.class)
.whenOneOf("userMoveType", Arrays.asList(SlideUserMoveType.WALK, SlideUserMoveType.SLIDE), u -> u
        .integer("userIndex")
        .string("userOldZ")
        .string("userNewZ"))
```

## Branching on part of a value

Some ints pack several things. A furni's stuff data starts with `typeAndFlags`: the low byte is the format, and bit 256 means a limited edition, so a serial number follows the format's values. Pass a mask to `branch` or `when` and only those bits decide. `branch(on, mask, cases)` picks the case by `on & mask`, and `when(on, mask, value, body)` adds parameters when `on & mask` equals `value`:

```java
.integer("typeAndFlags")
.branch("typeAndFlags", 0xFF, cases -> cases
        .on(0, LegacyStuffData.class, s -> s
                .string("legacyString")
                .when("typeAndFlags", 256, 256, serial -> serial
                        .integer("uniqueSerialNumber")
                        .integer("uniqueSeriesSize")))
        // ...
);
```

Bits outside the mask are kept in the value and written back unchanged, so a flag the library doesn't know yet doesn't break the packet. A case body can use a value declared before the branch, as the `when` above does with `typeAndFlags`.

The sign bit works the same way. A floor furni with a negative type sends its class name at the end, so its schema adds that name when the sign bit is set:

```java
.when("furniClassId", Integer.MIN_VALUE, Integer.MIN_VALUE, s -> s.string("staticClass"))
```

## Optional trailing parameters

Some servers leave parameters off the end of a packet. Wrap those in `optional`: they're read only when bytes are left, and written only when one of them is set.

```java
public static final PacketType<CatalogPublished> TYPE = PacketType.of("CatalogPublished", HMessage.Direction.TOCLIENT, Schema.of(CatalogPublished.class)
        .bool("instantlyRefreshCatalog")
        .optional(s -> s.string("newFurniDataHash")));
```

When the client checks for bytes left before each value, nest them, so the packet can stop after any of them: `.optional(a -> a.bool("chooserDisabled").optional(b -> b.bool("freeFurniMovementsEnabled")))`.

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

An enum turns a value it doesn't have into `null`, so that value would be lost when you write the packet again. When the client gets the values as data (new ones can appear without a client update), or you can't name every value it knows, extend `OpenIntEnum` instead. It reads like an enum, and keeps an id it doesn't name. Declare the ids you can name as `public static final` fields:

```java
public final class ChatBarStyle extends OpenIntEnum {
    public static final ChatBarStyle DEFAULT = new ChatBarStyle(0);
    public static final ChatBarStyle GENERIC = new ChatBarStyle(1);
    // ...

    private ChatBarStyle(int value) {
        super(value);
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ChatBarStyle of(int value) {
        return of(ChatBarStyle.class, value, ChatBarStyle::new);
    }

    public static List<ChatBarStyle> values() {
        return values(ChatBarStyle.class);
    }
}
```

The schema line stays the same: `.enumInt("style", ChatBarStyle.class)`. `OpenIntEnumTypesTest` checks that every open enum has this shape and that no two of its fields share an id. Keep a closed set, like `Direction`, a real enum: it still works in a `switch`. For a string code you can't fully name, keep a plain `String` and list the known codes in a comment.

## Parameters the client ignores

Sometimes the client reads a parameter and never uses it, declares an enum value it never acts on, or registers a packet and does nothing in its handler. Keep it in the schema, so the packet still parses and writes in full, and mark it with `@Unused` and `@Deprecated` together:

```java
@Unused("The client stores it but never reads it")
@Deprecated
private Boolean unknownBoolean12;
```

`@Deprecated` makes the compiler warn wherever an extension uses it; Lombok copies it onto the getter and the builder method. `@Unused` says why, for the packet reference and for tools. Mark an enum constant or a packet class the same way. `UnusedMarkerTest` fails when one of the two is missing.

Only mark what you've checked in the client's code: the value is stored and nothing reads it, or the handler does nothing with the packet. The client is only half the picture, though: a value it ignores can still matter on the server. `RoomSettingsData.maximumVisitorsLimit` is a limit the server uses, even though the client only logs it. If a value looks like server state, a limit or a setting, ask in the pull request before you mark it. Marking isn't a breaking change, so the pull request title needs no `!`. In your tests, put `@SuppressWarnings("deprecation")` on a test that sets or reads a marked parameter.

## Limits

When the client keeps a value within a limit, declare it in the schema, so extensions can't send something the client never would and tools can show it. Pass limits after the parameter's name, with a static import of `Limits`:

```java
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.*;

Schema.of(SaveRoomSettings.class)
        .string("name", maxLength(60))
        .list("tags", WireType.STRING, maxSize(2), each(maxLength(30)))
        .integer("idleSleepTimeoutSeconds", range(30, 3600).orZero(), requiresVip())
```

The factories are `maxLength(n)` and `notEmpty()` for strings, `range(min, max)` (with `.orZero()` when the client sends 0 for "off") for numbers, `maxSize(n)` and `each(limit)` for lists, `not(values...)` for values the client never sends, and `requiresVip()` for settings only VIP users can change. A limit on a parameter it doesn't fit fails when the class loads. For a limit across several parameters, add `.rule(description, values -> ...)`: it gets the values as they would be written, with an empty list or structure for one left out, and returns true when they're fine.

Only declare a limit the client never goes past: a fixed choice in its UI or a check before it sends. If the client sometimes goes past it, like the chat input's 100 characters, put it in a comment instead. Watch for values the client loads from the server and sends back: it resends them as it got them, so a limit only fits when the server keeps them within it too. Wired permission masks are an example: the client toggles four bits but sends back any other bit the server set. Add a comment on the field with the client evidence, and test one breaking value.

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
