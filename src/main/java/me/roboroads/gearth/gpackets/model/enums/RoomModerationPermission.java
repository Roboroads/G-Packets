package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * Who may mute, kick or ban in a room. Named after the client's localization keys
 * ({@code navigator.roomsettings.moderation.*}).
 */
@RequiredArgsConstructor
public enum RoomModerationPermission implements IntEnum {
    NONE(0),
    RIGHTS(1),
    ALL(2),
    GROUP_ADMINS(4),
    GROUP_ADMINS_AND_RIGHTS(5);

    @Getter
    @JsonValue
    private final int value;
}
