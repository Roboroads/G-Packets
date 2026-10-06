package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Asks for the room's wired errors; the server answers with {@code WiredErrorLogs}. The wired menu's
 * monitor tab sends it with {@code WiredGetRoomStats}. It has no parameters, so there's no
 * all-arguments constructor either.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class WiredGetErrorLogs implements Packet, JsonSerializable {
    public static final PacketType<WiredGetErrorLogs> TYPE = PacketType.of("WiredGetErrorLogs", HMessage.Direction.TOSERVER, Schema.of(WiredGetErrorLogs.class));

    public static WiredGetErrorLogs fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredGetErrorLogs fromJson(String json) {
        return Json.parse(WiredGetErrorLogs.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
