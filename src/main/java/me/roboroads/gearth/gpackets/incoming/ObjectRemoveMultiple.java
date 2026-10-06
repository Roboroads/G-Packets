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
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

/** Several floor furni leave the room at once. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ObjectRemoveMultiple implements Packet, JsonSerializable {
    public static final PacketType<ObjectRemoveMultiple> TYPE = PacketType.of("ObjectRemoveMultiple", HMessage.Direction.TOCLIENT, Schema.of(ObjectRemoveMultiple.class)
            .list("furniIds", WireType.INT)
            .integer("pickerUserId"));

    // The furni's ids in the room (FloorItem.furniId). The client calls them ids.
    private List<Integer> furniIds;
    // The account id of the user who picked them up. The client calls it pickerId; when it is your own
    // user id, the furni fly into your inventory icon.
    private Integer pickerUserId;

    public static ObjectRemoveMultiple fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ObjectRemoveMultiple fromJson(String json) {
        return Json.parse(ObjectRemoveMultiple.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
