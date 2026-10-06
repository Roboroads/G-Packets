package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.ClubLevel;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** Your club level, staff security level and ambassador status. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UserRights implements Packet, JsonSerializable {
    public static final PacketType<UserRights> TYPE = PacketType.of("UserRights", HMessage.Direction.TOCLIENT, Schema.of(UserRights.class)
            .enumInt("clubLevel", ClubLevel.class)
            .integer("securityLevel")
            .bool("isAmbassador"));

    // The session and the avatar editor treat every level but NONE as VIP.
    private ClubLevel clubLevel;
    // A plain number: the client's hasSecurity(level) is securityLevel >= level, and it checks
    // levels 1, 4, 5 and 7 for staff features (7 unlocks the staff-only room categories). No level
    // has a name in the client.
    private Integer securityLevel;
    private Boolean isAmbassador;

    public static UserRights fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UserRights fromJson(String json) {
        return Json.parse(UserRights.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
