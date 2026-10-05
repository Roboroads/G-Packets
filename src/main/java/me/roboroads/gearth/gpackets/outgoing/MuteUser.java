package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.RoomMuteDuration;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Mutes a user in a room for a while: the avatar menu's mute buttons (more of them in the
 * ambassador menu) and the {@code :mute <name>} chat command.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class MuteUser implements Packet, JsonSerializable {
    public static final PacketType<MuteUser> TYPE = PacketType.of("MuteUser", HMessage.Direction.TOSERVER, Schema.of(MuteUser.class)
            .integer("userId")
            .integer("roomId")
            .enumInt("duration", RoomMuteDuration.class));

    // The user's account id (User.id), not their room index: the client sends the user data's
    // webID.
    private Integer userId;
    // The client sends the room session's roomId, the room you're in.
    private Integer roomId;
    // In minutes. The composer takes it before roomId but writes it last.
    private RoomMuteDuration duration;

    public static MuteUser fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static MuteUser fromJson(String json) {
        return Json.parse(MuteUser.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
