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
 * Asks for the room's wired and furni usage; the server answers with {@code WiredRoomStats}. The
 * wired menu's monitor tab sends it with {@code WiredGetErrorLogs}. It has no parameters, so there's
 * no all-arguments constructor either.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class WiredGetRoomStats implements Packet, JsonSerializable {
    public static final PacketType<WiredGetRoomStats> TYPE = PacketType.of("WiredGetRoomStats", HMessage.Direction.TOSERVER, Schema.of(WiredGetRoomStats.class));

    public static WiredGetRoomStats fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredGetRoomStats fromJson(String json) {
        return Json.parse(WiredGetRoomStats.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
