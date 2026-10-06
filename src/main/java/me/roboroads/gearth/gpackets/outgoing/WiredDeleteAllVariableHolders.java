package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Removes a variable from every furni or user that holds it: the delete button of the wired menu's
 * variable overview, after the wiredmenu.variable_overview.delete_all confirmation. The client only
 * offers it for a persisted furni or user variable created by a user.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredDeleteAllVariableHolders implements Packet, JsonSerializable {
    public static final PacketType<WiredDeleteAllVariableHolders> TYPE = PacketType.of("WiredDeleteAllVariableHolders", HMessage.Direction.TOSERVER, Schema.of(WiredDeleteAllVariableHolders.class)
            .string("variableId"));

    // WiredVariable.variableId of the selected variable.
    private String variableId;

    public static WiredDeleteAllVariableHolders fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredDeleteAllVariableHolders fromJson(String json) {
        return Json.parse(WiredDeleteAllVariableHolders.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
