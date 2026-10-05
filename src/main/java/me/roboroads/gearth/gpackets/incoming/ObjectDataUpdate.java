package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.furni.StuffData;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** A floor furni's state changes, for example when someone uses it. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ObjectDataUpdate implements Packet, JsonSerializable {
    public static final PacketType<ObjectDataUpdate> TYPE = PacketType.of("ObjectDataUpdate", HMessage.Direction.TOCLIENT, Schema.of(ObjectDataUpdate.class)
            .string("furniId")
            .struct("stuffData", StuffData.SCHEMA));

    // The furni's id in the room (FloorItem.furniId), sent as a string; the client reads it with int().
    // The client calls it id.
    private String furniId;
    // The client calls it data, and reads the state number from its legacy string.
    private StuffData stuffData;

    public static ObjectDataUpdate fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ObjectDataUpdate fromJson(String json) {
        return Json.parse(ObjectDataUpdate.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
