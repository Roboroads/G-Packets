package me.roboroads.gearth.gpackets.incoming;

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
 * Everyone in the room you're in was muted or unmuted, for example after the outgoing
 * {@code MuteAllInRoom}.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class MuteAllInRoom implements Packet, JsonSerializable {
    public static final PacketType<MuteAllInRoom> TYPE = PacketType.of("MuteAllInRoom", HMessage.Direction.TOCLIENT, Schema.of(MuteAllInRoom.class)
            .bool("allMuted"));

    // The navigators store it on the entered room and refresh the room info buttons with it.
    private Boolean allMuted;

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
