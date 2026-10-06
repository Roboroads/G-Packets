package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.furni.FloorItem;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * A floor furni in the room changes, for example after it moved or turned. The client updates its
 * position, direction, state, extra, height and expiry from it.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ObjectUpdate implements Packet, JsonSerializable {
    public static final PacketType<ObjectUpdate> TYPE = PacketType.of("ObjectUpdate", HMessage.Direction.TOCLIENT, Schema.of(ObjectUpdate.class)
            .struct("object", FloorItem.SCHEMA));

    // The client calls it data.
    private FloorItem object;

    public static ObjectUpdate fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ObjectUpdate fromJson(String json) {
        return Json.parse(ObjectUpdate.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
