package me.roboroads.gearth.gpackets.incoming.sub.wired;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.WiredVariableAvailability;
import me.roboroads.gearth.gpackets.model.enums.WiredVariableTarget;
import me.roboroads.gearth.gpackets.model.enums.WiredVariableType;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/**
 * A wired variable's definition: the client's WiredVariable class. The wired menu packets and the
 * wired editor's context read it the same way.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredVariable implements SubPacket, JsonSerializable {
    public static final Schema<WiredVariable> SCHEMA = Schema.of(WiredVariable.class)
            .string("variableId")
            .enumInt("variableType", WiredVariableType.class)
            .string("variableName")
            .enumInt("availabilityType", WiredVariableAvailability.class)
            .enumInt("variableTarget", WiredVariableTarget.class)
            .bool("alwaysAvailable")
            .bool("canCreateAndDelete")
            .bool("hasValue")
            .bool("canWriteValue")
            .bool("canInterceptChanges")
            .bool("isInvisible")
            .bool("canReadCreationTime")
            .bool("canReadLastUpdateTime")
            .bool("hasTextConnector")
            .when("hasTextConnector", true, s -> s.list("textConnector", VariableTextConnection.SCHEMA));

    // The key every other wired menu packet uses for this variable.
    private String variableId;
    private WiredVariableType variableType;
    private String variableName;
    private WiredVariableAvailability availabilityType;
    private WiredVariableTarget variableTarget;
    private Boolean alwaysAvailable;
    private Boolean canCreateAndDelete;
    // False for a variable without a value: the wired menu then disables its value input and creates
    // the variable with 0.
    private Boolean hasValue;
    private Boolean canWriteValue;
    private Boolean canInterceptChanges;
    // The wired menu leaves an invisible variable out of its lists.
    private Boolean isInvisible;
    private Boolean canReadCreationTime;
    private Boolean canReadLastUpdateTime;
    private Boolean hasTextConnector;
    // The text the wired menu shows for a value, for each value that has one. Only on the wire when
    // hasTextConnector is true.
    private List<VariableTextConnection> textConnector;

    public static WiredVariable fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
