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

/** Redeems a clothing furni in the room. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class CustomizeAvatarWithFurni implements Packet, JsonSerializable {
    public static final PacketType<CustomizeAvatarWithFurni> TYPE = PacketType.of("CustomizeAvatarWithFurni", HMessage.Direction.TOSERVER, Schema.of(CustomizeAvatarWithFurni.class)
            .integer("furniId"));

    // The floor item's id (PurchasableClothingConfirmationView: getRoomObject(roomId, id, 10).getId()).
    private Integer furniId;

    public static CustomizeAvatarWithFurni fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static CustomizeAvatarWithFurni fromJson(String json) {
        return Json.parse(CustomizeAvatarWithFurni.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
