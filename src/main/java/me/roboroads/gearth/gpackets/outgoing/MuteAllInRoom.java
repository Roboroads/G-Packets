package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
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
 * Mutes or unmutes everyone in the room you're in: the room info's mute all button, which the
 * client shows when the room says you can mute and {@code room_moderation.mute_all.enabled} is on.
 * The server answers with the incoming {@code MuteAllInRoom}. It has no parameters.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class MuteAllInRoom implements Packet, JsonSerializable {
    public static final PacketType<MuteAllInRoom> TYPE = PacketType.of("MuteAllInRoom", HMessage.Direction.TOSERVER, Schema.of(MuteAllInRoom.class));

    public static MuteAllInRoom fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static MuteAllInRoom fromJson(String json) {
        return Json.parse(MuteAllInRoom.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
