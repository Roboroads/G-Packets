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
 * The answer to {@code Ping}: you're still connected. It has no parameters, so there's no
 * all-arguments constructor either.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class Pong implements Packet, JsonSerializable {
    public static final PacketType<Pong> TYPE = PacketType.of("Pong", HMessage.Direction.TOSERVER, Schema.of(Pong.class));

    public static Pong fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Pong fromJson(String json) {
        return Json.parse(Pong.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
