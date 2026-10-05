package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * How the variable management window sorts the users that hold a permanent variable. The value is
 * the selected item of its sort_type_menu dropdown, which lists
 * wiredmenu.variable_management.sort_by.0 to .5.
 */
@RequiredArgsConstructor
public enum WiredVariableSort implements IntEnum {
    // "Highest value"; the wired menu's manage button opens the window with it.
    HIGHEST_VALUE(0),
    // "Lowest value"
    LOWEST_VALUE(1),
    // "Oldest creation"
    OLDEST_CREATION(2),
    // "Latest creation"
    LATEST_CREATION(3),
    // "Oldest update"
    OLDEST_UPDATE(4),
    // "Latest update"
    LATEST_UPDATE(5);

    @Getter
    @JsonValue
    private final int value;

    public static WiredVariableSort fromValue(int value) {
        for (WiredVariableSort sort : values()) {
            if (sort.value == value) {
                return sort;
            }
        }
        return null;
    }
}
