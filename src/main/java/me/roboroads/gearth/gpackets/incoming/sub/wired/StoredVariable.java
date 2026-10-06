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
 * One permanent variable a user, pet or bot holds: the variable id, then its {@link VariableStorage}.
 * The client reads both with one WiredVariableStorageParameter, which only reads the id here.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class StoredVariable implements SubPacket, JsonSerializable {
    public static final Schema<StoredVariable> SCHEMA = Schema.of(StoredVariable.class)
            .string("variableId")
            .struct("storage", VariableStorage.SCHEMA);

    private String variableId;
    private VariableStorage storage;

    public static StoredVariable fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
