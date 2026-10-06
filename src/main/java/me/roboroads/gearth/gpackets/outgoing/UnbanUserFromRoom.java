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
 * Lifts a user's ban from a room: the unban button in the room settings' moderation tab. The server
 * answers with {@code UserUnbannedFromRoom}.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UnbanUserFromRoom implements Packet, JsonSerializable {
    public static final PacketType<UnbanUserFromRoom> TYPE = PacketType.of("UnbanUserFromRoom", HMessage.Direction.TOSERVER, Schema.of(UnbanUserFromRoom.class)
            .integer("userId")
            .integer("roomId"));

    // The account id of the selected BannedUser.
    private Integer userId;
    // The client sends the id of the room whose settings are open (its flatId).
    private Integer roomId;

    public static UnbanUserFromRoom fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UnbanUserFromRoom fromJson(String json) {
        return Json.parse(UnbanUserFromRoom.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
