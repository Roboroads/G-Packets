package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FrontPageItemType {
    PAGE_LINK(0),
    PRODUCT_OFFER(1),
    PRODUCT_CODE(2);

    @Getter
    @JsonValue
    private final int value;

    public static FrontPageItemType fromValue(int value) {
        for (FrontPageItemType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}