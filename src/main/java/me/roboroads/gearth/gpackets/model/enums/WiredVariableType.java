package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.roboroads.gearth.gpackets.support.schema.OpenIntEnum;

import java.util.List;

/**
 * How a wired variable came to be. The wired menu shows the text
 * wiredfurni.params.variables.idtype.&lt;type&gt; for any id, so {@link #of} keeps an id this list
 * doesn't name. The client's own constant for 1 is {@code INTERNAL}.
 */
public final class WiredVariableType extends OpenIntEnum {
    // wiredfurni.params.variables.idtype.0: "Created by user"
    public static final WiredVariableType CREATED_BY_USER = new WiredVariableType(0);
    // wiredfurni.params.variables.idtype.1: "Internal"
    public static final WiredVariableType INTERNAL = new WiredVariableType(1);
    // wiredfurni.params.variables.idtype.2: "Sub variable"
    public static final WiredVariableType SUB_VARIABLE = new WiredVariableType(2);
    // wiredfurni.params.variables.idtype.3: "Smart variable"
    public static final WiredVariableType SMART_VARIABLE = new WiredVariableType(3);

    private WiredVariableType(int value) {
        super(value);
    }

    /** The named type with this id, or a value that keeps it. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static WiredVariableType of(int value) {
        return of(WiredVariableType.class, value, WiredVariableType::new);
    }

    /** The named types, sorted by id. */
    public static List<WiredVariableType> values() {
        return values(WiredVariableType.class);
    }
}
