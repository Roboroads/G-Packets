package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// Mapping from the client's HabboClubLevelEnum. Wire-format is a plain int.
@RequiredArgsConstructor
public enum ClubLevel {
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