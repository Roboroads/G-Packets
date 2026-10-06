package me.roboroads.gearth.gpackets.incoming.sub.wired;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * A user, pet or bot that holds a permanent variable, with its stored value: one row of the variable
 * management window (the client's WiredUserVariablesElement).
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class VariableOwner implements SubPacket, JsonSerializable {
    public static final Schema<VariableOwner> SCHEMA = Schema.of(VariableOwner.class)
            .enumInt("entityType", UserType.class)
            .integer("entityId")
            .string("entityName")
            .struct("storage", VariableStorage.SCHEMA);

    // PLAYER, PET or BOT: the window shows the text wiredfurni.params.usertype.<type>.
    private UserType entityType;
    // The account id of a user (clicking the name sends GetExtendedProfile with it), or the pet or bot
    // id. Not a room index.
    private Integer entityId;
    private String entityName;
    private VariableStorage storage;

    public static VariableOwner fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
