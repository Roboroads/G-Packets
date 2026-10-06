package me.roboroads.gearth.gpackets.incoming.sub.room;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * A user banned from a room, in {@code BannedUsersFromRoom}. The room settings' moderation tab
 * lists them; its unban button sends the id back in {@code UnbanUserFromRoom}.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class BannedUser implements SubPacket, JsonSerializable {
    public static final Schema<BannedUser> SCHEMA = Schema.of(BannedUser.class)
            .integer("userId")
            .string("userName");

    // The account id.
    private Integer userId;
    private String userName;

    public static BannedUser fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
