package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.furni.WallItem;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * A wall furni in the room changes, for example after it moved. The client updates its location,
 * direction, state data and expiry from it.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdate implements Packet, JsonSerializable {
    public static final PacketType<ItemUpdate> TYPE = PacketType.of("ItemUpdate", HMessage.Direction.TOCLIENT, Schema.of(ItemUpdate.class)
            .struct("item", WallItem.SCHEMA));

    // The client calls it data.
    private WallItem item;

    public static ItemUpdate fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ItemUpdate fromJson(String json) {
        return Json.parse(ItemUpdate.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
