package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.wired.VariableHash;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/**
 * Asks for the room's variables that changed; the server answers with one or more
 * {@code WiredAllVariablesDiffs}. The client's WiredVariablesSynchronizer sends it when
 * {@code WiredAllVariablesHash} differs from the hash it has.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredGetAllVariablesDiffs implements Packet, JsonSerializable {
    public static final PacketType<WiredGetAllVariablesDiffs> TYPE = PacketType.of("WiredGetAllVariablesDiffs", HMessage.Direction.TOSERVER, Schema.of(WiredGetAllVariablesDiffs.class)
            .list("variableHashes", VariableHash.SCHEMA));

    // Client: variableIdToHash. The hash of every variable the client has, empty the first time.
    private List<VariableHash> variableHashes;

    public static WiredGetAllVariablesDiffs fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredGetAllVariablesDiffs fromJson(String json) {
        return Json.parse(WiredGetAllVariablesDiffs.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
