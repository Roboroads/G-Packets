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

/** A user's ban from a room was lifted, for example after {@code UnbanUserFromRoom}. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UserUnbannedFromRoom implements Packet, JsonSerializable {
    public static final PacketType<UserUnbannedFromRoom> TYPE = PacketType.of("UserUnbannedFromRoom", HMessage.Direction.TOCLIENT, Schema.of(UserUnbannedFromRoom.class)
            .integer("roomId")
            .integer("userId"));

    // The room settings ignore the packet unless it's for the room they show.
    private Integer roomId;
    // The account id, as in BannedUser.userId.
    private Integer userId;

    public static UserUnbannedFromRoom fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UserUnbannedFromRoom fromJson(String json) {
        return Json.parse(UserUnbannedFromRoom.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
