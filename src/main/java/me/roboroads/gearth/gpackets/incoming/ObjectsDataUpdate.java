package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.furni.FloorItemDataUpdate;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/** The state of several floor furni changes at once. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ObjectsDataUpdate implements Packet, JsonSerializable {
    public static final PacketType<ObjectsDataUpdate> TYPE = PacketType.of("ObjectsDataUpdate", HMessage.Direction.TOCLIENT, Schema.of(ObjectsDataUpdate.class)
            .list("objects", FloorItemDataUpdate.SCHEMA));

    private List<FloorItemDataUpdate> objects;

    public static ObjectsDataUpdate fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ObjectsDataUpdate fromJson(String json) {
        return Json.parse(ObjectsDataUpdate.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
