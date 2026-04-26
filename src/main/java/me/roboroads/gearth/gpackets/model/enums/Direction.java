package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Direction {
    NORTH(0),
    NORTH_EAST(1),
    EAST(2),
    SOUTH_EAST(3),
    SOUTH(4),
    SOUTH_WEST(5),
    WEST(6),
    NORTH_WEST(7);

    @Getter
    @JsonValue
    private final int value;

    public static Direction fromValue(int value) {
        for (Direction direction : values()) {
            if (direction.value == value) {
                return direction;
            }
        }
        return null;
    }
}
