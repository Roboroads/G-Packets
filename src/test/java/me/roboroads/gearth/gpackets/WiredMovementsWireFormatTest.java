package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.WiredMovements;
import me.roboroads.gearth.gpackets.incoming.sub.wired.FurniMove;
import me.roboroads.gearth.gpackets.incoming.sub.wired.UserDirectionUpdate;
import me.roboroads.gearth.gpackets.incoming.sub.wired.UserMove;
import me.roboroads.gearth.gpackets.incoming.sub.wired.WallItemMove;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.WiredMovementType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static me.roboroads.gearth.gpackets.WireAssert.assertSameBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WiredMovementsWireFormatTest {

    static WiredMovements sample() {
        return WiredMovements.builder().movements(Arrays.asList(
                UserMove.builder().movementType(WiredMovementType.USER_MOVE)
                        .sourceX(1).sourceY(2).targetX(3).targetY(4).sourceZ("0.0").targetZ("1.5")
                        .userIndex(7).isSlide(0).animationTime(500)
                        .bodyDirection(Direction.EAST).headDirection(Direction.SOUTH)
                        .hasJump(true).jumpPower(3)
                        .build(),
                UserMove.builder().movementType(WiredMovementType.USER_MOVE)
                        .sourceX(5).sourceY(6).targetX(7).targetY(8).sourceZ("0.0").targetZ("0.0")
                        .userIndex(2).isSlide(1).animationTime(250)
                        .bodyDirection(Direction.NORTH).headDirection(Direction.NORTH)
                        .hasJump(false)
                        .build(),
                FurniMove.builder().movementType(WiredMovementType.FURNI_MOVE)
                        .sourceX(1).sourceY(1).targetX(2).targetY(2).sourceZ("0.0").targetZ("0.5")
                        .furniId(1001).animationTime(500).rotation(Direction.WEST)
                        .hasOvershoot(true).overshootingDistance(2).hasCurve(false)
                        .build(),
                FurniMove.builder().movementType(WiredMovementType.FURNI_MOVE)
                        .sourceX(3).sourceY(3).targetX(4).targetY(4).sourceZ("1.0").targetZ("1.0")
                        .furniId(1002).animationTime(300).rotation(Direction.SOUTH_EAST)
                        .hasOvershoot(false).hasCurve(true).curveStrength(4)
                        .build(),
                WallItemMove.builder().movementType(WiredMovementType.WALL_ITEM_MOVE)
                        .itemId(55).isDirectionRight(true)
                        .oldWallX(1).oldWallY(2).oldOffsetX(3).oldOffsetY(4)
                        .newWallX(5).newWallY(6).newOffsetX(7).newOffsetY(8)
                        .animationTime(400)
                        .build(),
                UserDirectionUpdate.builder().movementType(WiredMovementType.USER_DIRECTION_UPDATE)
                        .userIndex(9).bodyDirection(Direction.NORTH_EAST).headDirection(Direction.SOUTH_WEST)
                        .build()
        )).build();
    }

    static HPacket expectedPacket() {
        HPacket p = new HPacket("WiredMovements", HMessage.Direction.TOCLIENT);
        p.appendInt(6);
        // UserMove with a jump
        p.appendInt(0).appendInt(1).appendInt(2).appendInt(3).appendInt(4).appendString("0.0").appendString("1.5")
                .appendInt(7).appendInt(0).appendInt(500).appendInt(2).appendInt(4)
                .appendBoolean(true).appendInt(3);
        // UserMove without a jump
        p.appendInt(0).appendInt(5).appendInt(6).appendInt(7).appendInt(8).appendString("0.0").appendString("0.0")
                .appendInt(2).appendInt(1).appendInt(250).appendInt(0).appendInt(0)
                .appendBoolean(false);
        // FurniMove with an overshoot, no curve
        p.appendInt(1).appendInt(1).appendInt(1).appendInt(2).appendInt(2).appendString("0.0").appendString("0.5")
                .appendInt(1001).appendInt(500).appendInt(6)
                .appendBoolean(true).appendInt(2).appendBoolean(false);
        // FurniMove with a curve, no overshoot
        p.appendInt(1).appendInt(3).appendInt(3).appendInt(4).appendInt(4).appendString("1.0").appendString("1.0")
                .appendInt(1002).appendInt(300).appendInt(3)
                .appendBoolean(false).appendBoolean(true).appendInt(4);
        // WallItemMove
        p.appendInt(2).appendInt(55).appendBoolean(true)
                .appendInt(1).appendInt(2).appendInt(3).appendInt(4)
                .appendInt(5).appendInt(6).appendInt(7).appendInt(8)
                .appendInt(400);
        // UserDirectionUpdate
        p.appendInt(3).appendInt(9).appendInt(1).appendInt(5);
        return p;
    }

    @Test
    void toPacketWritesTheWireFormat() {
        assertSameBytes(expectedPacket(), sample().toPacket());
    }

    @Test
    void fromPacketReadsTheWireFormat() {
        assertEquals(sample(), WiredMovements.fromPacket(expectedPacket()));
    }
}
