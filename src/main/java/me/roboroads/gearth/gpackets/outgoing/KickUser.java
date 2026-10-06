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
 * Kicks a user out of the room you're in: the avatar menu's kick buttons (also in the ambassador
 * menu) and the {@code :kick <name>} chat command.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class KickUser implements Packet, JsonSerializable {
    public static final PacketType<KickUser> TYPE = PacketType.of("KickUser", HMessage.Direction.TOSERVER, Schema.of(KickUser.class)
            .integer("userId"));

    // The user's account id (User.id), not their room index: the client sends the user data's
    // webID.
    private Integer userId;

    public static KickUser fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static KickUser fromJson(String json) {
        return Json.parse(KickUser.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
