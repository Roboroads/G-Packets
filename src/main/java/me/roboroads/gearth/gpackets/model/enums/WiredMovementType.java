package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WiredMovementType {
    USER_MOVE(0),
    FURNI_MOVE(1),
    WALL_ITEM_MOVE(2),
    USER_DIRECTION_UPDATE(3);

    @Getter
    @JsonValue
    private final int value;

    public static WiredMovementType fromValue(int value) {
        for (WiredMovementType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
