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
 * The server's half of the Diffie-Hellman key exchange, the answer to the outgoing
 * {@code CompleteDiffieHandshake}. The client derives the shared key from it, turns encryption
 * on and sends {@code VersionCheck}, {@code UniqueID} and {@code SSOTicket}.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class CompleteDiffieHandshake implements Packet, JsonSerializable {
    public static final PacketType<CompleteDiffieHandshake> TYPE = PacketType.of("CompleteDiffieHandshake", HMessage.Direction.TOCLIENT, Schema.of(CompleteDiffieHandshake.class)
            .string("encryptedPublicKey")
            .optional(s -> s.bool("serverClientEncryption")));

    // The server's public key, signed with the server's RSA key, as a hex string.
    private String encryptedPublicKey;
    // Only read when bytes are left. When true, the client also decrypts what the server sends;
    // otherwise only the client's packets are encrypted.
    private Boolean serverClientEncryption;

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
