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
    // The client calls it itemId.
    private Integer furniId;
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
