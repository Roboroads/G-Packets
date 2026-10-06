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
 * A variable the server added or changed, with its hash. The client's WiredVariablesSynchronizer
 * keeps the hash per variable id and sends it back in {@code WiredGetAllVariablesDiffs}.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class HashedVariable implements SubPacket, JsonSerializable {
    public static final Schema<HashedVariable> SCHEMA = Schema.of(HashedVariable.class)
            .integer("hash")
            .struct("variable", WiredVariable.SCHEMA);

    private Integer hash;
    private WiredVariable variable;

    public static HashedVariable fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
