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

/** Identifies the computer the client runs on. Sent once encryption is on, before {@code SSOTicket}. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UniqueID implements Packet, JsonSerializable {
    public static final PacketType<UniqueID> TYPE = PacketType.of("UniqueID", HMessage.Direction.TOSERVER, Schema.of(UniqueID.class)
            .string("machineId")
            .string("fingerprint")
            .string("flashVersion"));

    // The machine id from the client's local shared object ("machineid"), where UniqueMachineID
    // stores it.
    private String machineId;
    // CommunicationUtils.generateFingerprint().
    private String fingerprint;
    // Capabilities.version with its space turned into a slash, such as "WIN/32,0,0,465". G-Rust's
    // name.
    private String flashVersion;

    public static UniqueID fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UniqueID fromJson(String json) {
        return Json.parse(UniqueID.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
