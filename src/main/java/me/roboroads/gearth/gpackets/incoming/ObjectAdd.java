package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.furni.FloorItem;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** A floor furni appears in the room. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ObjectAdd implements Packet, JsonSerializable {
    public static final PacketType<ObjectAdd> TYPE = PacketType.of("ObjectAdd", HMessage.Direction.TOCLIENT, Schema.of(ObjectAdd.class)
            .struct("object", FloorItem.SCHEMA)
            .string("ownerName"));

    // The client calls it data.
    private FloorItem object;
    // The name of the object's owner (FloorItem.ownerId).
    private String ownerName;

    public static ObjectAdd fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ObjectAdd fromJson(String json) {
        return Json.parse(ObjectAdd.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
