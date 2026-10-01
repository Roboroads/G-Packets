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
            .integer("id")
            .integer("x")
            .integer("y")
            .string("z")
            .enumInt("dirHead", Direction.class)
            .enumInt("dir", Direction.class)
            .integer("jumpingPower")
            .string("actions");

    // The user's room index (User.roomIndex), not their account id.
    private Integer id;
    private Integer x;
    private Integer y;
    private String z;
    private Direction dirHead;
    private Direction dir;
    private Integer jumpingPower;
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
