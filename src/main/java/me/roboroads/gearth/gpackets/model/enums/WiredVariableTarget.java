package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * What a wired variable belongs to. The client's variable target constants name {@code FURNI} (0)
 * and {@code USER} (1), and VariableExtraSourceTypes names {@code GLOBAL_SOURCE} (-10) and
 * {@code CONTEXT_SOURCE} (-20). The wired menu's VariableTypePicker offers the same four: furni,
 * user, global and context.
 */
@RequiredArgsConstructor
public enum WiredVariableTarget implements IntEnum {
    FURNI(0),
    USER(1),
    // wiredfurni.params.sources.global: "Global variables"
    GLOBAL(-10),
    // wiredfurni.params.sources.context: "Context variables"
    CONTEXT(-20);

    @Getter
    @JsonValue
    private final int value;

    public static WiredVariableTarget fromValue(int value) {
        for (WiredVariableTarget target : values()) {
            if (target.value == value) {
                return target;
            }
        }
        return null;
    }
}
