package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.roboroads.gearth.gpackets.support.schema.OpenIntEnum;

import java.util.List;

/**
 * How long a wired variable keeps its value. The wired menu shows the text
 * wiredfurni.params.variables.availability.&lt;id&gt;, or "/" for an id without one. The client's
 * availability constants also declare 21, which has no text and stays unnamed, so {@link #of} keeps
 * it and any other id this list doesn't name. The client counts 10, 11 and 20 as persisted
 * ({@code WiredVariable.isPersisted}).
 */
public final class WiredVariableAvailability extends OpenIntEnum {
    // wiredfurni.params.variables.availability.0: "While user is in room". The client's constants
    // also call 0 NOT_APPLICABLE.
    public static final WiredVariableAvailability WHILE_USER_IN_ROOM = new WiredVariableAvailability(0);
    // wiredfurni.params.variables.availability.1: "While room is active"
    public static final WiredVariableAvailability WHILE_ROOM_ACTIVE = new WiredVariableAvailability(1);
    // wiredfurni.params.variables.availability.10: "Permanent"
    public static final WiredVariableAvailability PERMANENT = new WiredVariableAvailability(10);
    // wiredfurni.params.variables.availability.11: "Permanent, shared across rooms"
    public static final WiredVariableAvailability PERMANENT_SHARED = new WiredVariableAvailability(11);
    // wiredfurni.params.variables.availability.20: "Reference"
    public static final WiredVariableAvailability REFERENCE = new WiredVariableAvailability(20);

    private WiredVariableAvailability(int value) {
        super(value);
    }

    /** The named availability with this id, or a value that keeps it. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static WiredVariableAvailability of(int value) {
        return of(WiredVariableAvailability.class, value, WiredVariableAvailability::new);
    }

    /** The named availabilities, sorted by id. */
    public static List<WiredVariableAvailability> values() {
        return values(WiredVariableAvailability.class);
    }
}
