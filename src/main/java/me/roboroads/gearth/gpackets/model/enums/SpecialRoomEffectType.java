package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * A room-wide visual effect. The client's {@code RoomMessageHandler.onSpecialRoomEvent}
 * ({@code room/__ms.as}) handles these ids and ignores any other.
 */
@RequiredArgsConstructor
public enum SpecialRoomEffectType implements IntEnum {
    // RoomRotatingEffect, for 5 seconds.
    ROTATE(0),
    // RoomShakingEffect, for 5 seconds.
    SHAKE(1),
    // A RoomEngineZoomEvent with isFlipForced: turns the room view upside down, or back up again.
    FLIP(2),
    // Cycles the room colour through the client's discoColours list, one colour per second.
    DISCO(3);

    @Getter
    @JsonValue
    private final int value;

    public static SpecialRoomEffectType fromValue(int value) {
        for (SpecialRoomEffectType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
