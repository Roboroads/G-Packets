package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * How strictly a room limits chat flooding: the index into the room settings
 * {@code chat_flood_sensitivity} dropdown ({@code navigator.roomsettings.chat.flood.*}).
 */
@RequiredArgsConstructor
public enum ChatFloodSensitivity implements IntEnum {
    // "Extra anti-flood protection"
    STRICT(0),
    // "Standard anti-flood protection"
    NORMAL(1),
    // "Minimal anti-flood protection"
    LOOSE(2);

    @Getter
    @JsonValue
    private final int value;
}
