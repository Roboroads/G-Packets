package me.roboroads.gearth.gpackets.incoming.sub.wired;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * The hash of a variable the client already has, by variable id: one entry of the client's
 * variableIdToHash, which {@code WiredGetAllVariablesDiffs} sends.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class VariableHash implements SubPacket, JsonSerializable {
    public static final Schema<VariableHash> SCHEMA = Schema.of(VariableHash.class)
            .string("variableId")
            .integer("hash");

    private String variableId;
    // The hash from the HashedVariable the client got for this variable.
    private Integer hash;

    public static VariableHash fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
