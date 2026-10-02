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
 * Where the client was loaded from. The first packet the client sends once encryption is on,
 * before {@code UniqueID} and {@code SSOTicket}.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class VersionCheck implements Packet, JsonSerializable {
    public static final PacketType<VersionCheck> TYPE = PacketType.of("VersionCheck", HMessage.Direction.TOSERVER, Schema.of(VersionCheck.class)
            .integer("unknownInt1")
            .string("flashClientUrl")
            .string("externalVariablesUrl"));

    // No evidence: the client always sends 401, and G-Rust calls it _unknown.
    @Builder.Default
    private Integer unknownInt1 = 401;
    // The login component's flashClientUrl, its flash.client.url property. The client's
    // configuration sets it to "app:/" before it reads its start arguments.
    private String flashClientUrl;
    // The client's external.variables.txt property: the URL it loads the external variables from.
    // G-Rust calls it external_variables.
    private String externalVariablesUrl;

    public static VersionCheck fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static VersionCheck fromJson(String json) {
        return Json.parse(VersionCheck.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
