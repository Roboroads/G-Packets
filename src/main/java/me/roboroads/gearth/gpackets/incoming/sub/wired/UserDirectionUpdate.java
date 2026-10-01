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
public class UserDirectionUpdate extends WiredMovement {
    private Integer userIndex;
    private Direction bodyDirection;
    // The client's handler for this update turns both body and head to bodyDirection, so it ignores this value.
    // It does use headDirection for a wired user move, so this is real server data and isn't marked unused.
    private Direction headDirection;
}
