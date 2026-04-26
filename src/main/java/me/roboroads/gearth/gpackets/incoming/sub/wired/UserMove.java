package me.roboroads.gearth.gpackets.incoming.sub.wired;

import gearth.protocol.HPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.WiredMovementType;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class UserMove extends WiredMovement {
    private Integer sourceX;
    private Integer sourceY;
    private Integer targetX;
    private Integer targetY;
    private String sourceZ;
    private String targetZ;
    private Integer userIndex;
    private Integer isSlide;
    private Integer animationTime;
    private Direction bodyDirection;
    private Direction headDirection;
    private Boolean hasJump;
    private Integer jumpPower;

    public static UserMove fromPacket(HPacket packet, WiredMovementType movementType) {
        UserMoveBuilder<?, ?> builder = UserMove.builder()
                .movementType(movementType)
                .sourceX(packet.readInteger())
                .sourceY(packet.readInteger())
                .targetX(packet.readInteger())
                .targetY(packet.readInteger())
                .sourceZ(packet.readString())
                .targetZ(packet.readString())
                .userIndex(packet.readInteger())
                .isSlide(packet.readInteger())
                .animationTime(packet.readInteger())
                .bodyDirection(Direction.fromValue(packet.readInteger()))
                .headDirection(Direction.fromValue(packet.readInteger()));

        boolean hasJump = packet.readBoolean();
        builder.hasJump(hasJump);
        if (hasJump) {
            builder.jumpPower(packet.readInteger());
        }

        return builder.build();
    }

    @Override
    public void appendPacket(HPacket packet) {
        super.appendPacket(packet);
        packet.appendInt(sourceX);
        packet.appendInt(sourceY);
        packet.appendInt(targetX);
        packet.appendInt(targetY);
        packet.appendString(sourceZ);
        packet.appendString(targetZ);
        packet.appendInt(userIndex);
        packet.appendInt(isSlide);
        packet.appendInt(animationTime);
        packet.appendInt(bodyDirection != null ? bodyDirection.value() : -1);
        packet.appendInt(headDirection != null ? headDirection.value() : -1);
        packet.appendBoolean(hasJump);
        if (hasJump != null && hasJump) {
            packet.appendInt(jumpPower);
        }
    }
}
