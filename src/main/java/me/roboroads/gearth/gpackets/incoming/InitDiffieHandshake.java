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

/**
 * The Diffie-Hellman prime and generator, the answer to the outgoing {@code InitDiffieHandshake}.
 * The client checks them, makes its own key pair and answers with the outgoing
 * {@code CompleteDiffieHandshake}.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class InitDiffieHandshake implements Packet, JsonSerializable {
    public static final PacketType<InitDiffieHandshake> TYPE = PacketType.of("InitDiffieHandshake", HMessage.Direction.TOCLIENT, Schema.of(InitDiffieHandshake.class)
            .string("encryptedPrime")
            .string("encryptedGenerator"));

    // The prime, signed with the server's RSA key, as a hex string.
    private String encryptedPrime;
    // The generator, signed the same way.
    private String encryptedGenerator;

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
