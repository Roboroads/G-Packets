package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.room.BannedUser;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/** The users banned from a room, the answer to {@code GetBannedUsersFromRoom}. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class BannedUsersFromRoom implements Packet, JsonSerializable {
    public static final PacketType<BannedUsersFromRoom> TYPE = PacketType.of("BannedUsersFromRoom", HMessage.Direction.TOCLIENT, Schema.of(BannedUsersFromRoom.class)
            .integer("roomId")
            .list("bannedUsers", BannedUser.SCHEMA));

    // The room settings ignore the list unless it's for the room they show.
    private Integer roomId;
    private List<BannedUser> bannedUsers;

    public static BannedUsersFromRoom fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static BannedUsersFromRoom fromJson(String json) {
        return Json.parse(BannedUsersFromRoom.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
