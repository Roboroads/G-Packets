package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * The operating system the client says it runs on in {@code ClientHello}. The client's composer
 * ({@code __Ig/__N2w.as}) picks the value from {@code Capabilities.os}.
 */
@RequiredArgsConstructor
public enum OperatingSystem implements IntEnum {
    // Capabilities.os names none of the three below.
    OTHER(0),
    // Capabilities.os contains "Mac".
    MAC(5),
    // Capabilities.os contains "Windows".
    WINDOWS(6),
    // Capabilities.os contains "Linux".
    LINUX(7);

    @Getter
    @JsonValue
    private final int value;

    public static OperatingSystem fromValue(int value) {
        for (OperatingSystem system : values()) {
            if (system.value == value) {
                return system;
            }
        }
        return null;
    }
}
