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
public class FurniMove extends WiredMovement {
    private Integer sourceX;
    private Integer sourceY;
    private Integer targetX;
    private Integer targetY;
    private String sourceZ;
    private String targetZ;
    private Integer furniId;
    private Integer animationTime;
    private Direction rotation;
    private Boolean hasOvershoot;
    private Integer overshootingDistance;
    private Boolean hasCurve;
    private Integer curveStrength;

    public static FurniMove fromPacket(HPacket packet, WiredMovementType movementType) {
        FurniMoveBuilder<?, ?> builder = FurniMove.builder()
                .movementType(movementType)
                .sourceX(packet.readInteger())
                .sourceY(packet.readInteger())
                .targetX(packet.readInteger())
                .targetY(packet.readInteger())
                .sourceZ(packet.readString())
                .targetZ(packet.readString())
                .furniId(packet.readInteger())
                .animationTime(packet.readInteger())
                .rotation(Direction.fromValue(packet.readInteger()));

        boolean hasOvershoot = packet.readBoolean();
        builder.hasOvershoot(hasOvershoot);
        if (hasOvershoot) {
            builder.overshootingDistance(packet.readInteger());
        }

        boolean hasCurve = packet.readBoolean();
        builder.hasCurve(hasCurve);
        if (hasCurve) {
            builder.curveStrength(packet.readInteger());
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
        packet.appendInt(furniId);
        packet.appendInt(animationTime);
        packet.appendInt(rotation != null ? rotation.value() : -1);
        packet.appendBoolean(hasOvershoot);
        if (hasOvershoot != null && hasOvershoot) {
            packet.appendInt(overshootingDistance);
        }
        packet.appendBoolean(hasCurve);
        if (hasCurve != null && hasCurve) {
            packet.appendInt(curveStrength);
        }
    }
}
