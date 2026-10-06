package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.wired.VariableValue;
import me.roboroads.gearth.gpackets.model.enums.WiredVariableTarget;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

/**
 * The variables a furni, a user or the room holds, the answer to {@code WiredGetVariablesForObject}:
 * the client's WiredObjectInspectionData, which the wired menu's inspection tab shows.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredVariablesForObject implements Packet, JsonSerializable {
    public static final PacketType<WiredVariablesForObject> TYPE = PacketType.of("WiredVariablesForObject", HMessage.Direction.TOCLIENT, Schema.of(WiredVariablesForObject.class)
            .enumInt("variableTarget", WiredVariableTarget.class)
            .when("variableTarget", WiredVariableTarget.FURNI, s -> s.integer("objectId"))
            .when("variableTarget", WiredVariableTarget.USER, s -> s.integer("userIndex"))
            .list("variableValues", VariableValue.SCHEMA)
            .when("variableTarget", WiredVariableTarget.FURNI, s -> s.list("configuredInWireds", WireType.INT)));

    // Client: type. FURNI, USER or GLOBAL, as asked for.
    private WiredVariableTarget variableTarget;
    // The furni id. Only on the wire for FURNI.
    private Integer objectId;
    // The user's room index. Only on the wire for USER.
    private Integer userIndex;
    private List<VariableValue> variableValues;
    // The ids of the wired furni that use this furni; the inspection tab's highlight button marks
    // them. Only on the wire for FURNI.
    private List<Integer> configuredInWireds;

    public static WiredVariablesForObject fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredVariablesForObject fromJson(String json) {
        return Json.parse(WiredVariablesForObject.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
