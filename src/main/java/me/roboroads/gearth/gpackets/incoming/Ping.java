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
 * The server checks that you're still connected; the client answers with {@code Pong}. It has no
 * parameters, so there's no all-arguments constructor either.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class Ping implements Packet, JsonSerializable {
    public static final PacketType<Ping> TYPE = PacketType.of("Ping", HMessage.Direction.TOCLIENT, Schema.of(Ping.class));

    public static Ping fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Ping fromJson(String json) {
        return Json.parse(Ping.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
