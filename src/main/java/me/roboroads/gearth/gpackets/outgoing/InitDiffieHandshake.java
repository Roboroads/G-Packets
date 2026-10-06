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
 * Starts the Diffie-Hellman key exchange; the server answers with the incoming
 * {@code InitDiffieHandshake}. The client sends it right after {@code ClientHello}. It has no
 * parameters, so there's no all-arguments constructor either.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class InitDiffieHandshake implements Packet, JsonSerializable {
    public static final PacketType<InitDiffieHandshake> TYPE = PacketType.of("InitDiffieHandshake", HMessage.Direction.TOSERVER, Schema.of(InitDiffieHandshake.class));

    public static InitDiffieHandshake fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static InitDiffieHandshake fromJson(String json) {
        return Json.parse(InitDiffieHandshake.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
