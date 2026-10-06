package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Asks for the permanent variables of one user, pet or bot; the server answers with
 * {@code WiredUserPermanentVariables}. The variable management window sends it from a row's manage
 * button and from its refresh button.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredGetUserPermanentVariables implements Packet, JsonSerializable {
    public static final PacketType<WiredGetUserPermanentVariables> TYPE = PacketType.of("WiredGetUserPermanentVariables", HMessage.Direction.TOSERVER, Schema.of(WiredGetUserPermanentVariables.class)
            .enumInt("entityType", UserType.class)
            .integer("entityId"));

    // The entityType and entityId of a VariableOwner or of WiredUserPermanentVariables.
    private UserType entityType;
    // The account id of a user, or the pet or bot id. Not a room index.
    private Integer entityId;

    public static WiredGetUserPermanentVariables fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredGetUserPermanentVariables fromJson(String json) {
        return Json.parse(WiredGetUserPermanentVariables.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
