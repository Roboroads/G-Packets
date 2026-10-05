package me.roboroads.gearth.gpackets.incoming.sub.wired;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * The stored value of a permanent variable, with when it was created and last changed: the
 * client's WiredVariableStorageParameter, without the variable id it reads first in
 * {@code WiredUserPermanentVariables} (see {@link StoredVariable}).
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class VariableStorage implements SubPacket, JsonSerializable {
    public static final Schema<VariableStorage> SCHEMA = Schema.of(VariableStorage.class)
            .integer("value")
            .longValue("creationTime")
            .string("creationTimeStr")
            .longValue("lastUpdateTime")
            .string("lastUpdateTimeStr");

    private Integer value;
    // The client only compares the two times to notice a change; it shows the *Str texts, which the
    // server formats.
    private Long creationTime;
    private String creationTimeStr;
    private Long lastUpdateTime;
    private String lastUpdateTimeStr;

    public static VariableStorage fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
