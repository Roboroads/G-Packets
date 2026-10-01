package me.roboroads.gearth.gpackets.incoming.sub.user;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** The position, direction and actions of one user in {@code UserUpdate}. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateData implements SubPacket, JsonSerializable {
    public static final Schema<UserUpdateData> SCHEMA = Schema.of(UserUpdateData.class)
            .integer("userIndex")
            .integer("x")
            .integer("y")
            .string("z")
            .enumInt("headDirection", Direction.class)
            .enumInt("bodyDirection", Direction.class)
            .integer("jumpPower")
            .string("actions");

    // The user's room index (User.roomIndex), not their account id. The client calls it id.
    private Integer userIndex;
    private Integer x;
    private Integer y;
    private String z;
    // The client calls it dirHead.
    private Direction headDirection;
    // The client calls it dir.
    private Direction bodyDirection;
    // The client calls it jumpingPower and only uses it while the user moves.
    private Integer jumpPower;
    // Slash-separated actions, each a type and its parameters split by spaces, like
    // "/mv 3,4,0.0/sit 1.0 1/". The client reads "mv" (target x,y,z), "sit" (height, then "1" when
    // the user can stand up), "lay" (height) and "wf" (skip the position update).
    private String actions;

    public static UserUpdateData fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
