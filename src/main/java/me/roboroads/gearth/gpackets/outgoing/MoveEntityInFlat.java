package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Moves or turns a rentable bot in the room. The client only sends it for a room object of type
 * rentable_bot (sendMoveUserObjectMessage in the room's object event handler), when you move it with
 * the furni mover or rotate it.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class MoveEntityInFlat implements Packet, JsonSerializable {
    public static final PacketType<MoveEntityInFlat> TYPE = PacketType.of("MoveEntityInFlat", HMessage.Direction.TOSERVER, Schema.of(MoveEntityInFlat.class)
            .integer("userIndex")
            .integer("x")
            .integer("y")
            .enumInt("direction", Direction.class));

    // The bot's room index (User.userIndex), not its id: the client sends the room object's id, which
    // it looks up with getUserDataByIndex for the other user objects it moves.
    private Integer userIndex;
    // The room object's location x.
    private Integer x;
    // The room object's location y.
    private Integer y;
    // The room object's direction in degrees divided by 45.
    private Direction direction;

    public static MoveEntityInFlat fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static MoveEntityInFlat fromJson(String json) {
        return Json.parse(MoveEntityInFlat.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
