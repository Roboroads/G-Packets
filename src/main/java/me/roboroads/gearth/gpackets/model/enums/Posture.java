package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/** A posture you can ask for: the avatar menu's "stand" and "sit" buttons (OwnAvatarMenuView). */
@RequiredArgsConstructor
public enum Posture implements IntEnum {
    STAND(0),
    SIT(1);

    @Getter
    @JsonValue
    private final int value;

    public static Posture fromValue(int value) {
        for (Posture posture : values()) {
            if (posture.value == value) {
                return posture;
            }
        }
        return null;
    }
}
