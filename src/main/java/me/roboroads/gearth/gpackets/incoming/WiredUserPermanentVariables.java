package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.wired.StoredVariable;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/**
 * The permanent variables of one user, pet or bot, the answer to
 * {@code WiredGetUserPermanentVariables}: the client's WiredUserPermanentVariablesList. A pet or bot
 * also comes with its owner.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredUserPermanentVariables implements Packet, JsonSerializable {
    public static final PacketType<WiredUserPermanentVariables> TYPE = PacketType.of("WiredUserPermanentVariables", HMessage.Direction.TOCLIENT, Schema.of(WiredUserPermanentVariables.class)
            .enumInt("entityType", UserType.class)
            .integer("entityId")
            .string("entityName")
            .string("entityFigure")
            .whenNot("entityType", UserType.PLAYER, s -> s
                    .integer("ownerId")
                    .string("ownerName")
                    .string("ownerFigure"))
            .list("variables", StoredVariable.SCHEMA));

    // PLAYER, PET or BOT, as in VariableOwner.
    private UserType entityType;
    // The account id of a user, or the pet or bot id. Not a room index.
    private Integer entityId;
    private String entityName;
    // The look the variable management window previews.
    private String entityFigure;
    // The pet's or bot's owner, shown as "Owner id" and "Owner name". Only on the wire when
    // entityType isn't PLAYER.
    private Integer ownerId;
    private String ownerName;
    @Unused("The client stores it as ownerFigure but nothing reads it")
    @Deprecated
    private String ownerFigure;
    // Client: variableStorage.
    private List<StoredVariable> variables;

    public static WiredUserPermanentVariables fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredUserPermanentVariables fromJson(String json) {
        return Json.parse(WiredUserPermanentVariables.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
