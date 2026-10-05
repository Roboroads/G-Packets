package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.WiredVariableAction;
import me.roboroads.gearth.gpackets.model.enums.WiredVariableTarget;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Sets, creates or deletes a variable on the furni, user or room the wired menu's inspection tab
 * shows. It needs the write permission.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredSetObjectVariableValue implements Packet, JsonSerializable {
    public static final PacketType<WiredSetObjectVariableValue> TYPE = PacketType.of("WiredSetObjectVariableValue", HMessage.Direction.TOSERVER, Schema.of(WiredSetObjectVariableValue.class)
            .enumInt("variableTarget", WiredVariableTarget.class)
            .integer("objectId")
            .string("variableId")
            .integer("value")
            .enumInt("action", WiredVariableAction.class));

    // WiredVariable.variableTarget of the variable.
    private WiredVariableTarget variableTarget;
    // As in WiredGetVariablesForObject: the furni id, the user's room index, or 0 for the room.
    private Integer objectId;
    private String variableId;
    // The new value for SET_VALUE and CREATE (0 for a variable without a value), 0 for DELETE.
    private Integer value;
    private WiredVariableAction action;

    public static WiredSetObjectVariableValue fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredSetObjectVariableValue fromJson(String json) {
        return Json.parse(WiredSetObjectVariableValue.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
