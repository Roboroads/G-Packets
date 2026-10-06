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
 * One entry of a wired variable's text connector: the text the wired menu shows for a value (its
 * variable overview has a value and a text column for them).
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class VariableTextConnection implements SubPacket, JsonSerializable {
    public static final Schema<VariableTextConnection> SCHEMA = Schema.of(VariableTextConnection.class)
            .integer("value")
            .string("text");

    private Integer value;
    private String text;

    public static VariableTextConnection fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
