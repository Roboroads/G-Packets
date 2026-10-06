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
 * Gives a user rights in the room you're in: the avatar menu's "Give rights" button, or a friend
 * clicked in the room settings' rights tab. The server answers with {@code FlatControllerAdded}.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class AssignRights implements Packet, JsonSerializable {
    public static final PacketType<AssignRights> TYPE = PacketType.of("AssignRights", HMessage.Direction.TOSERVER, Schema.of(AssignRights.class)
            .integer("userId"));

    // The user's account id (User.id), not their room index: the avatar menu sends the user data's
    // webID, the room settings a friend's user id.
    private Integer userId;

    public static AssignRights fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static AssignRights fromJson(String json) {
        return Json.parse(AssignRights.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
