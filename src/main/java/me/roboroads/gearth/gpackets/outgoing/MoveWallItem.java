package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** Moves a wall furni in the room. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class MoveWallItem implements Packet, JsonSerializable {
    public static final PacketType<MoveWallItem> TYPE = PacketType.of("MoveWallItem", HMessage.Direction.TOSERVER, Schema.of(MoveWallItem.class)
            .integer("furniId")
            .string("location"));

    // The furni's id in the room (WallItem.furniId, as an int).
    private Integer furniId;
    // The new place on the wall, in the format of WallItem.location (":w=3,5 l=12,30 r"); the client
    // builds it with getOldLocationString. G-Rust's name. The composer also takes the category (20) but
    // doesn't send it.
    private String location;

    public static MoveWallItem fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static MoveWallItem fromJson(String json) {
        return Json.parse(MoveWallItem.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
