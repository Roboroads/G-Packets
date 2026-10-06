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

/** You watch the room as a spectator instead of entering it. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class YouAreSpectator implements Packet, JsonSerializable {
    public static final PacketType<YouAreSpectator> TYPE = PacketType.of("YouAreSpectator", HMessage.Direction.TOCLIENT, Schema.of(YouAreSpectator.class)
            .integer("roomId"));

    // The client calls it flatId; it puts that room's session in spectator mode.
    private Integer roomId;

    public static YouAreSpectator fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static YouAreSpectator fromJson(String json) {
        return Json.parse(YouAreSpectator.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
