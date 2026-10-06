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
 * Uses a floor furni, for example when you double-click it. Furni widgets send it too: effect boxes,
 * mystery boxes, mannequins, friend furni, area hide and background color furni, the playlist editor,
 * and RoomSession.plantSeed.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UseFurniture implements Packet, JsonSerializable {
    public static final PacketType<UseFurniture> TYPE = PacketType.of("UseFurniture", HMessage.Direction.TOSERVER, Schema.of(UseFurniture.class)
            .integer("furniId")
            .integer("param"));

    // The furni's id in the room (FloorItem.furniId).
    private Integer furniId;
    // What the furni's logic asks for: RoomObjectStateChangeEvent.param, 0 by default. Furni with
    // several buttons send others (the hockey score board 1, 2 or 3, fireworks 0 to 2, the jukebox -1),
    // and the playlist editor sends -2 or a song's position.
    @Builder.Default
    private Integer param = 0;

    public static UseFurniture fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UseFurniture fromJson(String json) {
        return Json.parse(UseFurniture.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
