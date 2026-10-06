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

/** A furni or user in the room that holds a variable, and its value: the client's ObjectIdAndValuePair. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class VariableHolder implements SubPacket, JsonSerializable {
    public static final Schema<VariableHolder> SCHEMA = Schema.of(VariableHolder.class)
            .integer("objectId")
            .integer("value");

    // The furni id for a furni variable, the user's room index for a user variable (the wired menu
    // highlights the user it finds with getUserDataByIndex).
    private Integer objectId;
    // Ignored by the wired menu when the variable has no value (WiredVariable.hasValue).
    private Integer value;

    public static VariableHolder fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
