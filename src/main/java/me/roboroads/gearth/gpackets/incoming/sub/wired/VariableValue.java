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

/** A variable a furni, a user or the room holds, by variable id, with its value. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class VariableValue implements SubPacket, JsonSerializable {
    public static final Schema<VariableValue> SCHEMA = Schema.of(VariableValue.class)
            .string("variableId")
            .integer("value");

    private String variableId;
    private Integer value;

    public static VariableValue fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
