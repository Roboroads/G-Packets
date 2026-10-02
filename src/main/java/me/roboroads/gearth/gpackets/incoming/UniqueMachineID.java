package me.roboroads.gearth.gpackets.incoming;

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
 * A machine id for this computer. The client saves it and sends it back in {@code UniqueID} on
 * the next connect.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UniqueMachineID implements Packet, JsonSerializable {
    public static final PacketType<UniqueMachineID> TYPE = PacketType.of("UniqueMachineID", HMessage.Direction.TOCLIENT, Schema.of(UniqueMachineID.class)
            .string("machineId"));

    // The client calls it machineID and writes it to its local shared object as "machineid".
    private String machineId;

    public static UniqueMachineID fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UniqueMachineID fromJson(String json) {
        return Json.parse(UniqueMachineID.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
