package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.wired.HashedVariable;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

/**
 * The room's variables that changed since the hashes the client sent in
 * {@code WiredGetAllVariablesDiffs}, possibly in several chunks. The client's
 * WiredVariablesSynchronizer applies them to its cache of the room's variables.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredAllVariablesDiffs implements Packet, JsonSerializable {
    public static final PacketType<WiredAllVariablesDiffs> TYPE = PacketType.of("WiredAllVariablesDiffs", HMessage.Direction.TOCLIENT, Schema.of(WiredAllVariablesDiffs.class)
            .integer("allVariablesHash")
            .bool("isLastChunk")
            .list("removedVariableIds", WireType.STRING)
            .list("addedOrUpdated", HashedVariable.SCHEMA));

    // The hash of all the room's variables, as in WiredAllVariablesHash.
    private Integer allVariablesHash;
    // The client waits for more chunks until this is true.
    private Boolean isLastChunk;
    // Client: removedVariables. The client deletes these variable ids from its cache.
    private List<String> removedVariableIds;
    private List<HashedVariable> addedOrUpdated;

    public static WiredAllVariablesDiffs fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredAllVariablesDiffs fromJson(String json) {
        return Json.parse(WiredAllVariablesDiffs.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
