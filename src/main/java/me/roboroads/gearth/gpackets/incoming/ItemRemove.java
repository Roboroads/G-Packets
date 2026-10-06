package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** A wall furni leaves the room. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ItemRemove implements Packet, JsonSerializable {
    public static final PacketType<ItemRemove> TYPE = PacketType.of("ItemRemove", HMessage.Direction.TOCLIENT, Schema.of(ItemRemove.class)
            .string("furniId")
            .integer("pickerUserId"));

    // The furni's id in the room (WallItem.furniId), sent as a string; the client reads it with int().
    // The client calls it itemId.
    private String furniId;
    // The account id of the user who picked it up. The client calls it pickerId; when it is your own
    // user id, the furni flies into your inventory icon.
    private Integer pickerUserId;

    public static ItemRemove fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ItemRemove fromJson(String json) {
        return Json.parse(ItemRemove.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
