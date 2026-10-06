package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.roboroads.gearth.gpackets.support.schema.OpenIntEnum;

import java.util.List;

/**
 * What happened to a word in your custom word filter. The ModifyCustomFilterResult event declares
 * 0, 1 and 3; {@code WordFilterSettingsView.onModifyCustomFilter} adds the word to its list on 1 and
 * removes it on 3. Nothing acts on 0, so it has no name; {@link #of} keeps it and any other id.
 */
public final class CustomFilterResult extends OpenIntEnum {
    public static final CustomFilterResult ADDED = new CustomFilterResult(1);
    public static final CustomFilterResult REMOVED = new CustomFilterResult(3);

    private CustomFilterResult(int value) {
        super(value);
    }

    /** The named result with this id, or a value that keeps it. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CustomFilterResult of(int value) {
        return of(CustomFilterResult.class, value, CustomFilterResult::new);
    }

    /** The named results, sorted by id. */
    public static List<CustomFilterResult> values() {
        return values(CustomFilterResult.class);
    }
}
