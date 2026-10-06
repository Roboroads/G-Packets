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

/** Gives the hand item you carry to a pet in the room. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class PassCarryItemToPet implements Packet, JsonSerializable {
    public static final PacketType<PassCarryItemToPet> TYPE = PacketType.of("PassCarryItemToPet", HMessage.Direction.TOSERVER, Schema.of(PassCarryItemToPet.class)
            .integer("petId"));

    // The pet's id (User.id), not its room index: InfoStandWidgetHandler sends
    // RoomWidgetUserActionMessage.userId and looks it up with UserDataManager.getPetUserData.
    private Integer petId;

    public static PassCarryItemToPet fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static PassCarryItemToPet fromJson(String json) {
        return Json.parse(PassCarryItemToPet.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
