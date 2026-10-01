package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

// Values and names from the client's purse constants (com.sulake.habbo.catalog.purse).
// The wire only carries the int, so callers may keep the raw int and treat this as
// documentation only.
@RequiredArgsConstructor
public enum ActivityPointType implements IntEnum {
    DUCKETS(0),
    NO_OP_1(1),
    NO_OP_2(2),
    // Client constant is obfuscated and has no descriptive usage, unverified.
    UNKNOWN_3(3),
    NO_OP_4(4),
    // Client constant is obfuscated; its icon style is gated by the "diamonds.enabled" property.
    DIAMONDS(5),
    CREDITS(7),
    SEASONAL_1(101),
    SEASONAL_2(102),
    SEASONAL_3(103),
    SEASONAL_4(104),
    SEASONAL_5(105),
    SILVER(1000),
    EMERALD(1001);

    @Getter
    @JsonValue
    private final int value;

    public static ActivityPointType fromValue(int value) {
        for (ActivityPointType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
