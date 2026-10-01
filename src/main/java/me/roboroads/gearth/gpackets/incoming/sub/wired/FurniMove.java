package me.roboroads.gearth.gpackets.incoming.sub.wired;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Direction;

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
}
