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

/** A floor furni leaves the room. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ObjectRemove implements Packet, JsonSerializable {
    public static final PacketType<ObjectRemove> TYPE = PacketType.of("ObjectRemove", HMessage.Direction.TOCLIENT, Schema.of(ObjectRemove.class)
            .string("furniId")
            .bool("isExpired")
            .integer("pickerUserId")
            .integer("delayMilliseconds"));

    // The furni's id in the room (FloorItem.furniId), sent as a string; the client reads it with int().
    // The client calls it id.
    private String furniId;
    // True when a rented furni expired. The client then ignores pickerUserId.
    private Boolean isExpired;
    // The account id of the user who picked it up. The client calls it pickerId; when it is your own
    // user id, the furni flies into your inventory icon.
    private Integer pickerUserId;
    // How long the client waits before it removes the furni (a setTimeout). The client calls it delay.
    private Integer delayMilliseconds;

    public static ObjectRemove fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ObjectRemove fromJson(String json) {
        return Json.parse(ObjectRemove.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
