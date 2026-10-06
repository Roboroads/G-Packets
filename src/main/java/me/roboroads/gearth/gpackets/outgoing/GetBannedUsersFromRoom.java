package me.roboroads.gearth.gpackets.outgoing;

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

/**
 * Asks for the users banned from a room; the server answers with {@code BannedUsersFromRoom}. The
 * room settings send it when their moderation tab opens.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class GetBannedUsersFromRoom implements Packet, JsonSerializable {
    public static final PacketType<GetBannedUsersFromRoom> TYPE = PacketType.of("GetBannedUsersFromRoom", HMessage.Direction.TOSERVER, Schema.of(GetBannedUsersFromRoom.class)
            .integer("roomId"));

    private Integer roomId;

    public static GetBannedUsersFromRoom fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static GetBannedUsersFromRoom fromJson(String json) {
        return Json.parse(GetBannedUsersFromRoom.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
