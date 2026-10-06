package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.WiredVariableTarget;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Asks for the variables a furni, a user or the room holds; the server answers with
 * {@code WiredVariablesForObject}. The wired menu's inspection tab sends it when you pick a furni or
 * user, or the global type, and again every half second while it shows them.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredGetVariablesForObject implements Packet, JsonSerializable {
    public static final PacketType<WiredGetVariablesForObject> TYPE = PacketType.of("WiredGetVariablesForObject", HMessage.Direction.TOSERVER, Schema.of(WiredGetVariablesForObject.class)
            .enumInt("variableTarget", WiredVariableTarget.class)
            .integer("objectId"));

    // FURNI, USER or GLOBAL, the type selected in the inspection tab.
    private WiredVariableTarget variableTarget;
    // The inspection tab's getObjectIdForType: the furni id for FURNI, the user's room index for
    // USER, 0 for GLOBAL.
    private Integer objectId;

    public static WiredGetVariablesForObject fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredGetVariablesForObject fromJson(String json) {
        return Json.parse(WiredGetVariablesForObject.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
