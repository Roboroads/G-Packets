package me.roboroads.gearth.gpackets.incoming.sub.wired;

import gearth.protocol.HPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.WiredMovementType;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class WallItemMove extends WiredMovement {
    private Integer itemId;
    private Boolean isDirectionRight;
    private Integer oldWallX;
    private Integer oldWallY;
    private Integer oldOffsetX;
    private Integer oldOffsetY;
    private Integer newWallX;
    private Integer newWallY;
    private Integer newOffsetX;
    private Integer newOffsetY;
    private Integer animationTime;

    public static WallItemMove fromPacket(HPacket packet, WiredMovementType movementType) {
        return WallItemMove.builder()
                .movementType(movementType)
                .itemId(packet.readInteger())
                .isDirectionRight(packet.readBoolean())
                .oldWallX(packet.readInteger())
                .oldWallY(packet.readInteger())
                .oldOffsetX(packet.readInteger())
                .oldOffsetY(packet.readInteger())
                .newWallX(packet.readInteger())
                .newWallY(packet.readInteger())
                .newOffsetX(packet.readInteger())
                .newOffsetY(packet.readInteger())
                .animationTime(packet.readInteger())
                .build();
    }

    @Override
    public void appendPacket(HPacket packet) {
        super.appendPacket(packet);
        packet.appendInt(itemId);
        packet.appendBoolean(isDirectionRight);
        packet.appendInt(oldWallX);
        packet.appendInt(oldWallY);
        packet.appendInt(oldOffsetX);
        packet.appendInt(oldOffsetY);
        packet.appendInt(newWallX);
        packet.appendInt(newWallY);
        packet.appendInt(newOffsetX);
        packet.appendInt(newOffsetY);
        packet.appendInt(animationTime);
    }
}
