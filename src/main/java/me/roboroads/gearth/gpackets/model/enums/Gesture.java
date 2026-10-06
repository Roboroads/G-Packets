package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * The face a user or pet makes while it talks. The id is the index into the gesture map of the
 * avatar action class in {@code avatar/enum}; names from its {@code GESTURE_*} and
 * {@code PET_GESTURE_*} constants.
 */
@RequiredArgsConstructor
public enum Gesture implements IntEnum {
    NONE(0),
    // "sml"
    SMILE(1),
    // "agr"
    AGGRAVATED(2),
    // "srp"
    SURPRISED(3),
    // "sad"
    SAD(4),
    // "joy"
    PET_JOY(5),
    // "crz"
    PET_CRAZY(6),
    // "tng"
    PET_TONGUE(7),
    // "eyb"
    PET_BLINK(8),
    // "mis"
    PET_MISERABLE(9),
    // "puz"
    PET_PUZZLED(10);

    @Getter
    @JsonValue
    private final int value;
}
