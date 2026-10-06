package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.furni.WallItem;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** A wall furni appears in the room. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ItemAdd implements Packet, JsonSerializable {
    public static final PacketType<ItemAdd> TYPE = PacketType.of("ItemAdd", HMessage.Direction.TOCLIENT, Schema.of(ItemAdd.class)
            .struct("item", WallItem.SCHEMA)
            .string("ownerName"));

    // The client calls it data.
    private WallItem item;
    // The name of the item's owner (WallItem.ownerId).
    private String ownerName;

    public static ItemAdd fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ItemAdd fromJson(String json) {
        return Json.parse(ItemAdd.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
