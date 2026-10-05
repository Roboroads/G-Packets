package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * How long a room mute lasts, in minutes. The client only sends these: the avatar menu's mute
 * buttons (handled in {@code InfoStandWidgetHandler}) and the {@code :mute} chat command, which
 * always mutes for 2 minutes.
 */
@RequiredArgsConstructor
public enum RoomMuteDuration implements IntEnum {
    // infostand.button.mute_2min: "Mute for 2 min". Also the :mute and :shutup chat commands.
    MINUTES_2(2),
    // infostand.button.mute_5min: "Mute for 5 min". Only in the room controller's mute menu.
    MINUTES_5(5),
    // infostand.button.mute_10min: "Mute for 10 min"
    MINUTES_10(10),
    // The client only sends 15 minutes and longer from the ambassador menu.
    // infostand.button.mute_15min: "Mute for 15 min"
    MINUTES_15(15),
    // infostand.button.mute_60min: "Mute for 60 min"
    MINUTES_60(60),
    // infostand.button.mute_18hour: "Mute for 18 hours"
    HOURS_18(1080),
    // infostand.button.mute_36hour: "Mute for 36 hours"
    HOURS_36(2160),
    // infostand.button.mute_72hour: "Mute for 72 hours"
    HOURS_72(4320);

    @Getter
    @JsonValue
    private final int value;

    public static RoomMuteDuration fromValue(int value) {
        for (RoomMuteDuration duration : values()) {
            if (duration.value == value) {
                return duration;
            }
        }
        return null;
    }
}
