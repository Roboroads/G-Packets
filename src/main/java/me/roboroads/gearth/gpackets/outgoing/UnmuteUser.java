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

/** Lifts a user's mute in a room. The client only sends it from the ambassador menu's unmute button. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UnmuteUser implements Packet, JsonSerializable {
    public static final PacketType<UnmuteUser> TYPE = PacketType.of("UnmuteUser", HMessage.Direction.TOSERVER, Schema.of(UnmuteUser.class)
            .integer("userId")
            .integer("roomId"));

    // The user's account id (User.id), not their room index: the client sends the user data's
    // webID.
    private Integer userId;
    // The client sends the room session's roomId, the room you're in.
    private Integer roomId;

    public static UnmuteUser fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UnmuteUser fromJson(String json) {
        return Json.parse(UnmuteUser.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
