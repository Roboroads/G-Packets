package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * Whether a {@code BuildersClubPlacementWarning} repeats a floor or a wall placement. The parser
 * declares 0 and 1 and reads x, y and direction for 0 and a wall location otherwise. The client
 * answers 0 with {@code BuildersClubPlaceRoomItem} and anything else with
 * {@code BuildersClubPlaceWallItem}.
 */
@RequiredArgsConstructor
public enum BuildersClubPlacementType implements IntEnum {
    FLOOR(0),
    WALL(1);

    @Getter
    @JsonValue
    private final int value;

    public static BuildersClubPlacementType fromValue(int value) {
        for (BuildersClubPlacementType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
