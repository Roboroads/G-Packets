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

/** Uses a wall furni, for example when you double-click it. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UseWallItem implements Packet, JsonSerializable {
    public static final PacketType<UseWallItem> TYPE = PacketType.of("UseWallItem", HMessage.Direction.TOSERVER, Schema.of(UseWallItem.class)
            .integer("furniId")
            .integer("param"));

    // The furni's id in the room (WallItem.furniId, as an int).
    private Integer furniId;
    // What the furni's logic asks for: RoomObjectStateChangeEvent.param, 0 by default.
    @Builder.Default
    private Integer param = 0;

    public static UseWallItem fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UseWallItem fromJson(String json) {
        return Json.parse(UseWallItem.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
