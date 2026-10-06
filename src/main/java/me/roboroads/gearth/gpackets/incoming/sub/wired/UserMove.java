package me.roboroads.gearth.gpackets.incoming.sub.wired;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;

@CheckedAgainst("WIN63-202609091217-117204808")
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
}
