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
 * A user with rights in a room, in {@code FlatControllers} and {@code FlatControllerAdded}. The room
 * settings list them under "users with rights" and leave them out of the friends you can give
 * rights to.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class FlatController implements SubPacket, JsonSerializable {
    public static final Schema<FlatController> SCHEMA = Schema.of(FlatController.class)
            .integer("userId")
            .string("userName");

    // The account id: the room settings compare it with your friends' user ids, and send it back
    // in RemoveRights.
    private Integer userId;
    private String userName;

    public static FlatController fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
