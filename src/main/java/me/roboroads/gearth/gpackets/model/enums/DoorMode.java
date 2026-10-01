package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/** Who may enter a room. The client names each value with a {@code navigator.door.mode.*} key. */
@RequiredArgsConstructor
public enum DoorMode implements IntEnum {
    OPEN(0),
    // navigator.door.mode.closed: "Doorbell"
    DOORBELL(1),
    PASSWORD(2),
    // Hidden in the navigator from users without rights.
    INVISIBLE(3),
    // navigator.door.mode.noobs_only: "Only for new users". The client also lets ambassadors and
    // room controllers in. Room settings have no button for it.
    NEW_USERS_ONLY(4);

    @Getter
    @JsonValue
    private final int value;
}
