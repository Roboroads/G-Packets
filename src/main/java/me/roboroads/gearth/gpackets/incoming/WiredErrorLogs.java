package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.wired.WiredError;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/** The errors the room's wired ran into, the answer to {@code WiredGetErrorLogs}. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredErrorLogs implements Packet, JsonSerializable {
    public static final PacketType<WiredErrorLogs> TYPE = PacketType.of("WiredErrorLogs", HMessage.Direction.TOCLIENT, Schema.of(WiredErrorLogs.class)
            .list("errors", WiredError.SCHEMA));

    private List<WiredError> errors;

    public static WiredErrorLogs fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredErrorLogs fromJson(String json) {
        return Json.parse(WiredErrorLogs.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
