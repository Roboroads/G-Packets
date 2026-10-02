package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
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
 * The client's half of the Diffie-Hellman key exchange, the answer to the incoming
 * {@code InitDiffieHandshake}. The server answers with the incoming {@code CompleteDiffieHandshake}.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class CompleteDiffieHandshake implements Packet, JsonSerializable {
    public static final PacketType<CompleteDiffieHandshake> TYPE = PacketType.of("CompleteDiffieHandshake", HMessage.Direction.TOSERVER, Schema.of(CompleteDiffieHandshake.class)
            .string("encryptedPublicKey"));

    // The client's public key, encrypted with the server's RSA key, as a hex string. The composer
    // calls it publicKey; the name here matches the incoming packet's.
    private String encryptedPublicKey;

    public static CompleteDiffieHandshake fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static CompleteDiffieHandshake fromJson(String json) {
        return Json.parse(CompleteDiffieHandshake.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
