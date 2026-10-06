package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * How a roller moves the user on it, in SlideObjectBundle. The client's parser reads the user only for
 * 1 and 2, and logs "Incompatible character movetype!" for any value other than 0, 1 and 2.
 */
@RequiredArgsConstructor
public enum SlideUserMoveType implements IntEnum {
    // No user moves along: nothing follows.
    NONE(0),
    // The client's "mv": it gives the user the walking posture ("mv") while it moves.
    WALK(1),
    // The client's "sld": the user keeps its posture while it moves, and a walking user stands ("std").
    SLIDE(2);

    @Getter
    @JsonValue
    private final int value;

    public static SlideUserMoveType fromValue(int value) {
        for (SlideUserMoveType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
