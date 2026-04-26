package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserType {
    PLAYER(1),
    PET(2),
    OLD_BOT(3),
    BOT(4);

    @Getter
    @JsonValue
    private final int value;

    public static UserType fromValue(int value) {
        for (UserType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
