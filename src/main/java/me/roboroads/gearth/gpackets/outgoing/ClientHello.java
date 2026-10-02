package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.OperatingSystem;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * The first packet of a connection, before encryption starts: which client build connects, and
 * on which operating system.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ClientHello implements Packet, JsonSerializable {
    public static final PacketType<ClientHello> TYPE = PacketType.of("ClientHello", HMessage.Direction.TOSERVER, Schema.of(ClientHello.class)
            .string("releaseVersion")
            .string("clientType")
            .enumInt("operatingSystem", OperatingSystem.class)
            .integer("unknownInt4"));

    // The client's build, a constant in the composer such as "WIN63-202609161723-93809945". G-Rust
    // calls it version.
    private String releaseVersion;
    // A constant in the composer: "FLASH29". G-Rust's name.
    private String clientType;
    // G-Rust's name.
    private OperatingSystem operatingSystem;
    // No evidence: the composer always sends 4, and G-Rust calls it _unknown.
    @Builder.Default
    private Integer unknownInt4 = 4;

    public static ClientHello fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ClientHello fromJson(String json) {
        return Json.parse(ClientHello.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
