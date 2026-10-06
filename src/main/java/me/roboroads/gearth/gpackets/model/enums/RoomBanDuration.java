package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.StringEnum;

/**
 * How long a room ban lasts. The client sends the name of the avatar menu action as the code
 * ({@code RoomWidgetUserActionMessage.BAN_USER_HOUR}, {@code BAN_USER_DAY} and
 * {@code BAN_USER_PERM}), and its ban menu has only these three buttons.
 */
@RequiredArgsConstructor
public enum RoomBanDuration implements StringEnum {
    // infostand.button.ban_hour: "Ban for an hour"
    HOUR("RWUAM_BAN_USER_HOUR"),
    // infostand.button.ban_day: "Ban for a day"
    DAY("RWUAM_BAN_USER_DAY"),
    // infostand.button.perm_ban: "Ban permanently"
    PERMANENT("RWUAM_BAN_USER_PERM");

    @Getter
    @JsonValue
    private final String code;

    public static RoomBanDuration fromCode(String code) {
        if (code == null) return null;
        for (RoomBanDuration duration : values()) {
            if (duration.code.equals(code)) {
                return duration;
            }
        }
        return null;
    }
}
