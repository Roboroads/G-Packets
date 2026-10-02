package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** Whether you're playing a game in the current room. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class YouArePlayingGame implements Packet, JsonSerializable {
    public static final PacketType<YouArePlayingGame> TYPE = PacketType.of("YouArePlayingGame", HMessage.Direction.TOCLIENT, Schema.of(YouArePlayingGame.class)
            .bool("isPlaying"));

    // Passed to RoomEngine.setIsPlayingGame for the current room.
    private Boolean isPlaying;

    public static YouArePlayingGame fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static YouArePlayingGame fromJson(String json) {
        return Json.parse(YouArePlayingGame.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
