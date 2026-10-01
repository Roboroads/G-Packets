package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CatalogType {
    NORMAL("NORMAL"),
    BUILDERS_CLUB("BUILDERS_CLUB");

    @Getter
    @JsonValue
    private final String code;

    public static CatalogType fromCode(String code) {
        if (code == null) return null;
        for (CatalogType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}