package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.model.enums.WiredVariableAction;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Sets, creates or deletes a permanent variable of a user, pet or bot, from the variable management
 * window; the server answers with {@code WiredSetUserPermanentVariableResult}. It needs the write
 * permission.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredSetUserPermanentVariable implements Packet, JsonSerializable {
    public static final PacketType<WiredSetUserPermanentVariable> TYPE = PacketType.of("WiredSetUserPermanentVariable", HMessage.Direction.TOSERVER, Schema.of(WiredSetUserPermanentVariable.class)
            .enumInt("entityType", UserType.class)
            .integer("entityId")
            .string("variableId")
            .integer("value")
            .enumInt("action", WiredVariableAction.class));

    // The entityType and entityId of WiredUserPermanentVariables.
    private UserType entityType;
    // The account id of a user, or the pet or bot id. Not a room index.
    private Integer entityId;
    private String variableId;
    // The new value for SET_VALUE and CREATE (0 for a variable without a value), 0 for DELETE.
    private Integer value;
    private WiredVariableAction action;

    public static WiredSetUserPermanentVariable fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredSetUserPermanentVariable fromJson(String json) {
        return Json.parse(WiredSetUserPermanentVariable.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
