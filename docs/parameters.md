# Packet parameters

## Introduction

Every packet type carries a schema: its parameters in the order they appear on the wire, each with a name and a type. G-Packets uses the schema to read and write the typed classes, and you may use it directly when you want to handle packets generically, for example in a packet logger, an inspector, or an editor where someone changes values by hand.

Most extensions never need this page. If you know which packet you want, the typed classes from [Intercepting packets](intercepting.md) are easier to work with. Every schema is also listed in the [packet reference](packets/index.md).

```java
Map<String, Object> values = Users.TYPE.read(message.getPacket());
```

## Reading a packet as values

`TYPE.read` turns a packet into a `Map<String, Object>`, with the keys in wire order. It reads from the start of the body and puts the read index back afterwards, so other handlers can still read the same packet.

```java
Map<String, Object> values = Users.TYPE.read(message.getPacket());
// {users=[{id=1, name=Alice, custom=motto, ..., dir=2, type=1, sex=F, groupId=7, ...}]}
```

The values follow a few rules:

- A value is a `String` or a boxed primitive: `Integer`, `Boolean`, `Short`, `Long` or `Byte`.
- A list is a `List`, and a nested structure is a `Map` of its own.
- An enum holds its wire value, so `dir` is `2`, not `EAST`. A value the library doesn't know yet survives a read and a write unchanged.
- The values of a branch, such as a player's `sex` and `groupId`, sit in the same map as the values around them.
- A conditional value that wasn't sent, such as a wired movement's `jumpPower` when `hasJump` is false, is missing from the map.

## Changing values and putting them back

To change an intercepted packet without its typed class, read it, change the values, and hand them to `TYPE.replaceIn`. The message keeps its original header id, so G-Earth forwards it as usual.

```java
intercept(Users.TYPE.direction(), Users.TYPE.header(), message -> {
    Map<String, Object> values = Users.TYPE.read(message.getPacket());
    for (Object user : (List<?>) values.get("users")) {
        ((Map<String, Object>) user).put("name", "Hidden");
    }
    Users.TYPE.replaceIn(message, values);
});
```

!!! warning
    Don't copy the bytes of `TYPE.write(values)` into an intercepted message. A packet built from a header name has header id 0 until G-Earth sends it, so the client would get a packet it doesn't recognise. Use `replaceIn` instead.

## Building a packet from values

`TYPE.write` builds a new packet from values. Send it with G-Earth's `sendToServer` or `sendToClient`:

```java
Map<String, Object> values = new HashMap<>();
values.put("text", "Hello from values");
values.put("style", ChatBarStyle.DEFAULT);

sendToServer(Chat.TYPE.write(values));
```

Writing is forgiving where it can be:

- A missing or null value is written as `0`, `""` or `false`. The example leaves out `trackingId`, so `0` is sent.
- Numbers are narrowed to the wire type, so an `Integer` works for a `short`.
- An enum parameter accepts the constant (`ChatBarStyle.DEFAULT`) or its wire value (`0`).
- Keys the schema doesn't use are ignored. You may switch a user's `type` from player to bot and leave the player's keys in the map: only the bot's parameters are written.

A branch can't guess, though. If a user's `type` is missing or null, writing throws.

## Errors

When reading or writing fails, G-Packets throws an `IllegalArgumentException` whose message starts with the path to the value:

```text
Users.users[3].type: no case for value 7
Chat.style: expected INT, got java.lang.String
Users.users[0].name: packet ended before this STRING could be read
```

You get one for a branch value without a case (or a null one when writing), a value of the wrong Java type, and a packet that ends before the schema does.

## Finding the type of any packet

So far you've picked the packet type yourself. A generic tool doesn't know the type up front: `PacketTypes.all()` returns every packet type G-Packets implements, and `PacketTypes.find` looks one up by direction and header name. Together with G-Earth's packet info, that lets one listener handle every packet G-Packets knows:

```java
import gearth.protocol.HMessage;
import gearth.services.packet_info.PacketInfo;
import me.roboroads.gearth.gpackets.support.PacketTypes;

@Override
protected void initExtension() {
    for (HMessage.Direction direction : HMessage.Direction.values()) {
        intercept(direction, message -> {
            PacketInfo info = getPacketInfoManager()
                    .getPacketInfoFromHeaderId(message.getDestination(), message.getPacket().headerId());
            if (info == null) {
                return;
            }
            PacketTypes.find(message.getDestination(), info.getName()).ifPresent(type -> {
                System.out.println(info.getName() + " " + type.read(message.getPacket()));
            });
        });
    }
}
```

The loop registers the listener for both directions, so you see what the server sends and what your client sends.

`getPacketInfoManager()` comes from G-Earth and only knows the header names while you are connected, so look packets up inside the listener, not in `initExtension` itself.

## Listing the parameters

`TYPE.schema().parameters()` returns the parameters in wire order. Each one is one of five kinds:

| Kind | What it is | What you can ask it |
|---|---|---|
| `ValueParameter` | one value | `wireType()`, `enumType()`, `enumOptions()` |
| `ListParameter` | an int count, then that many elements | `elementType()` for values, `elementSchema()` for structures |
| `StructParameter` | a nested structure | `schema()` |
| `BranchParameter` | parameters that depend on an earlier value | `on()`, `exhaustive()`, `cases()` |
| `OptionalParameter` | parameters the server may leave off the end | `schema()` |

Every parameter has a `name()`, except branches and optionals, whose parameters sit in the surrounding values. A branch's `cases()` maps each wire value to a case with `value()`, `subclass()` and `schema()`. `exhaustive()` is false for a conditional parameter, where a value without a case simply adds nothing.

This method prints any schema as an indented tree:

```java
import me.roboroads.gearth.gpackets.support.schema.BranchParameter;
import me.roboroads.gearth.gpackets.support.schema.ListParameter;
import me.roboroads.gearth.gpackets.support.schema.OptionalParameter;
import me.roboroads.gearth.gpackets.support.schema.Parameter;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.StructParameter;
import me.roboroads.gearth.gpackets.support.schema.ValueParameter;

void describe(Schema<?> schema, String indent, Set<Schema<?>> seen) {
    if (!seen.add(schema)) {
        System.out.println(indent + "(" + schema.type().getSimpleName() + ", see above)");
        return;
    }
    for (Parameter parameter : schema.parameters()) {
        if (parameter instanceof ValueParameter) {
            ValueParameter value = (ValueParameter) parameter;
            System.out.println(indent + value.name() + ": " + value.wireType() + " " + value.enumOptions());
        } else if (parameter instanceof ListParameter) {
            ListParameter list = (ListParameter) parameter;
            System.out.println(indent + list.name() + ": list");
            if (list.elementSchema() != null) {
                describe(list.elementSchema(), indent + "  ", seen);
            }
        } else if (parameter instanceof StructParameter) {
            System.out.println(indent + parameter.name() + ":");
            describe(((StructParameter) parameter).schema(), indent + "  ", seen);
        } else if (parameter instanceof BranchParameter) {
            BranchParameter branch = (BranchParameter) parameter;
            branch.cases().forEach((value, c) -> {
                System.out.println(indent + "when " + branch.on() + " = " + value + ":");
                describe(c.schema(), indent + "  ", seen);
            });
        } else if (parameter instanceof OptionalParameter) {
            System.out.println(indent + "optional:");
            describe(((OptionalParameter) parameter).schema(), indent + "  ", seen);
        }
    }
}
```

Call it with a fresh set:

```java
describe(CatalogIndex.TYPE.schema(), "", Collections.newSetFromMap(new IdentityHashMap<>()));
```

!!! warning
    A schema can contain itself: a `CatalogNode` has `children` that are `CatalogNode`s. When you walk schemas, keep a set of the ones you've seen, compared by identity, as the example does. Without it the walk never ends.

## Typed objects from a schema

`TYPE.parse(packet)` and `TYPE.toPacket(object)` are the typed counterparts of `read` and `write`, and they are what `@Intercept` and a packet's `toPacket()` use. For a sub-structure, `User.SCHEMA.parse(packet)` and `User.SCHEMA.append(user, packet)` work from the packet's current read index.
