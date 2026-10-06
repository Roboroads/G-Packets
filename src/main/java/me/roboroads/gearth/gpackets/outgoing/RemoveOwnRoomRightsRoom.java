package me.roboroads.gearth.gpackets.outgoing;

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
 * Gives up your own rights in the room you're in: the room info's remove rights button, which the
 * client only shows when you have rights but don't own the room.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RemoveOwnRoomRightsRoom implements Packet, JsonSerializable {
    public static final PacketType<RemoveOwnRoomRightsRoom> TYPE = PacketType.of("RemoveOwnRoomRightsRoom", HMessage.Direction.TOSERVER, Schema.of(RemoveOwnRoomRightsRoom.class)
            .integer("roomId"));

    // The client sends the entered room's flatId (the navigator's removeRoomRights).
    private Integer roomId;

    public static RemoveOwnRoomRightsRoom fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RemoveOwnRoomRightsRoom fromJson(String json) {
        return Json.parse(RemoveOwnRoomRightsRoom.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
