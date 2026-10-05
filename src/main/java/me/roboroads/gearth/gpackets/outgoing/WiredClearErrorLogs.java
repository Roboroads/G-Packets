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
 * Clears the room's wired error logs: the clear button of the wired menu's monitor tab, which needs
 * the write permission. It has no parameters, so there's no all-arguments constructor either.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class WiredClearErrorLogs implements Packet, JsonSerializable {
    public static final PacketType<WiredClearErrorLogs> TYPE = PacketType.of("WiredClearErrorLogs", HMessage.Direction.TOSERVER, Schema.of(WiredClearErrorLogs.class));

    public static WiredClearErrorLogs fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredClearErrorLogs fromJson(String json) {
        return Json.parse(WiredClearErrorLogs.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
