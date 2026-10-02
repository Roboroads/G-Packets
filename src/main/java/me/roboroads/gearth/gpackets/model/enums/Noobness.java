package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * How new the user is, from {@code NoobnessLevel}. The values are the client's
 * {@code communication/enum/NoobnessLevelEnum.as}.
 */
@RequiredArgsConstructor
public enum Noobness implements IntEnum {
    // The client's name is obfuscated. Its isNoob is false only for this value.
    NOT_NOOB(0),
    // The client's name. Any value but 0 sets the new.identity property, which can change the
    // landing view's widgets, and makes isNoob true (the room tools start collapsed).
    NEW_IDENTITY(1),
    // The client's name. Only this value makes isRealNoob true, which turns on the new user lobbies
    // and lets the navigator open rooms that are only for new users (DoorMode.NEW_USERS_ONLY).
    REAL_NOOB(2);

    @Getter
    @JsonValue
    private final int value;

    public static Noobness fromValue(int value) {
        for (Noobness noobness : values()) {
            if (noobness.value == value) {
                return noobness;
            }
        }
        return null;
    }
}
