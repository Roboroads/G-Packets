package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.roboroads.gearth.gpackets.support.schema.OpenIntEnum;

import java.util.List;

/**
 * Why you can't enter a room. The client's parser declares 1 to 5, and the navigator's
 * onCantConnect shows an alert per value. It has no case for 2 (it falls through to the generic
 * "Cannot enter room."), so {@link #of} keeps 2 and any other id this list doesn't name.
 */
public final class CantConnectReason extends OpenIntEnum {
    // navigator.guestroomfull.text: "Sorry, the room you tried to enter is full."
    public static final CantConnectReason ROOM_FULL = new CantConnectReason(1);
    // room.queue.error.<parameter>, with the code in CantConnect.parameter.
    public static final CantConnectReason QUEUE_ERROR = new CantConnectReason(3);
    // navigator.banned.text: "Sorry, but the owner of this room has banned you."
    public static final CantConnectReason BANNED = new CantConnectReason(4);
    // navigator.blocked.text: "Sorry, but the owner of this room has blocked you."
    public static final CantConnectReason BLOCKED = new CantConnectReason(5);

    private CantConnectReason(int value) {
        super(value);
    }

    /** The named reason with this id, or a value that keeps it. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CantConnectReason of(int value) {
        return of(CantConnectReason.class, value, CantConnectReason::new);
    }

    /** The named reasons, sorted by id. */
    public static List<CantConnectReason> values() {
        return values(CantConnectReason.class);
    }
}
