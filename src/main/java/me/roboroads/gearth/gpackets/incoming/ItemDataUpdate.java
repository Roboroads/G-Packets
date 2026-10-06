package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * A wall furni's item data, such as a stickie's color and text. The client asks for it with
 * GetItemData when you use a stickie, and opens the stickie when it arrives.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ItemDataUpdate implements Packet, JsonSerializable {
    public static final PacketType<ItemDataUpdate> TYPE = PacketType.of("ItemDataUpdate", HMessage.Direction.TOCLIENT, Schema.of(ItemDataUpdate.class)
            .string("furniId")
            .string("itemData"));

    // The furni's id in the room (WallItem.furniId), sent as a string; the client reads it with int().
    // The client calls it id.
    private String furniId;
    // The client stores it as furniture_itemdata. For a stickie it is "<color> <text>": the stickie
    // widget splits it at the first space into a hex color (like 9CCEFF) and the text, and ignores it
    // when it is shorter than 6 characters.
    private String itemData;

    public static ItemDataUpdate fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ItemDataUpdate fromJson(String json) {
        return Json.parse(ItemDataUpdate.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
