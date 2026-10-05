package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** Moves or turns a floor furni in the room. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class MoveObject implements Packet, JsonSerializable {
    public static final PacketType<MoveObject> TYPE = PacketType.of("MoveObject", HMessage.Direction.TOSERVER, Schema.of(MoveObject.class)
            .integer("furniId")
            .integer("x")
            .integer("y")
            .enumInt("direction", Direction.class));

    // The furni's id in the room (FloorItem.furniId).
    private Integer furniId;
    // The tile to move to: the furni's location x. A rotation sends the current tile.
    private Integer x;
    // The tile to move to: the furni's location y.
    private Integer y;
    // The furni's direction in degrees divided by 45. G-Rust's name.
    private Direction direction;

    public static MoveObject fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static MoveObject fromJson(String json) {
        return Json.parse(MoveObject.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
