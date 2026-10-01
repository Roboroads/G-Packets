package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * The thickness of a room's walls or floor. The client sends its dropdown index minus 2
 * ({@code navigator.roomsettings.wall_thickness.*} and {@code floor_thickness.*}).
 */
@RequiredArgsConstructor
public enum RoomThickness implements IntEnum {
    THINNEST(-2),
    THIN(-1),
    NORMAL(0),
    THICK(1);

    @Getter
    @JsonValue
    private final int value;
}
