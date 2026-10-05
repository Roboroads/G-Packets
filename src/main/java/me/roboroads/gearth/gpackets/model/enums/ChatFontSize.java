package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * The chat text size you pick in the chat style selector. Names from its {@code FONT_SIZE_LABELS};
 * the scale from the free flow chat's {@code chatFontSizeScale}. The client clamps the setting to
 * 0..4.
 */
@RequiredArgsConstructor
public enum ChatFontSize implements IntEnum {
    // 1x
    S(0),
    // 1.15x
    M(1),
    // 1.3x
    L(2),
    // 1.5x
    XL(3),
    // 1.75x
    XXL(4);

    @Getter
    @JsonValue
    private final int value;
}
