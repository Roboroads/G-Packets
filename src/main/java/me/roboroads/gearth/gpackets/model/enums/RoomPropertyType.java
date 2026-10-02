package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.StringEnum;

/**
 * Which part of a room a {@code RoomProperty} sets. The client's parser ({@code __Q2t/__En.as})
 * switches on exactly these codes and ignores any other.
 */
@RequiredArgsConstructor
public enum RoomPropertyType implements StringEnum {
    FLOOR("floor"),
    WALLPAPER("wallpaper"),
    LANDSCAPE("landscape"),
    // The parser stores it as animatedLandscapeType, which nothing in the client reads.
    LANDSCAPE_ANIMATION("landscapeanim");

    @Getter
    @JsonValue
    private final String code;

    public static RoomPropertyType fromCode(String code) {
        if (code == null) return null;
        for (RoomPropertyType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
