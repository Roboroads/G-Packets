package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.StringEnum;

@RequiredArgsConstructor
public enum ProductType implements StringEnum {
    ITEM("i"),
    STUFF("s"),
    EFFECT("e"),
    BADGE("b"),
    // Declared in client but semantic meaning unconfirmed; likely "clothing".
    CL("cl"),
    // Client renders its icon via getSubscriptionProductIcon().
    SUBSCRIPTION("h"),
    // No client constant; rendered as an avatar from the figure in extraParam (likely a bot), unverified.
    R("r"),
    HABBICON("habbicon"),
    CHAT_STYLE("chat_style");

    @Getter
    @JsonValue
    private final String code;

    public static ProductType fromCode(String code) {
        if (code == null) return null;
        for (ProductType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}