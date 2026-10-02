package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

// Mapping from the client's club level enum. Wire-format is a plain int.
@RequiredArgsConstructor
public enum ClubLevel implements IntEnum {
    NONE(0),
    CLUB(1),
    VIP(2);

    @Getter
    @JsonValue
    private final int value;

    public static ClubLevel fromValue(int value) {
        for (ClubLevel level : values()) {
            if (level.value == value) {
                return level;
            }
        }
        return null;
    }
}