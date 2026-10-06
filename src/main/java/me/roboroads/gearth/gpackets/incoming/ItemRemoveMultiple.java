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
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

/** Several wall furni leave the room at once. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ItemRemoveMultiple implements Packet, JsonSerializable {
    public static final PacketType<ItemRemoveMultiple> TYPE = PacketType.of("ItemRemoveMultiple", HMessage.Direction.TOCLIENT, Schema.of(ItemRemoveMultiple.class)
            .list("furniIds", WireType.INT)
            .integer("pickerUserId"));

    // The furni's ids in the room (WallItem.furniId), as ints here. The client calls them itemIds.
    private List<Integer> furniIds;
    // The account id of the user who picked them up. The client calls it pickerId; when it is your own
    // user id, the furni fly into your inventory icon.
    private Integer pickerUserId;

    public static ItemRemoveMultiple fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ItemRemoveMultiple fromJson(String json) {
        return Json.parse(ItemRemoveMultiple.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
