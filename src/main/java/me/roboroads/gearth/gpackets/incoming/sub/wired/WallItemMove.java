package me.roboroads.gearth.gpackets.incoming.sub.wired;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

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
}
