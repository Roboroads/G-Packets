package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.StringEnum;

@RequiredArgsConstructor
public enum Gender implements StringEnum {
    MALE("M"),
    FEMALE("F");

    @Getter
    @JsonValue
    private final String code;

    public static Gender fromCode(String code) {
        if (code == null) return null;
        for (Gender gender : values()) {
            if (gender.code.equalsIgnoreCase(code)) {
                return gender;
            }
        }
        return null;
    }
}
