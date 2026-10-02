package me.roboroads.gearth.gpackets.incoming;

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
 * You left the room, or the server closed your connection to it. It has no parameters, so there's
 * no all-arguments constructor either.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class CloseConnection implements Packet, JsonSerializable {
    public static final PacketType<CloseConnection> TYPE = PacketType.of("CloseConnection", HMessage.Direction.TOCLIENT, Schema.of(CloseConnection.class));

    public static CloseConnection fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static CloseConnection fromJson(String json) {
        return Json.parse(CloseConnection.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
