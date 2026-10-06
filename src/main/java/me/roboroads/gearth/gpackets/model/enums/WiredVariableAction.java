package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * What to do with a variable on a furni, a user or the room. The WiredSetObjectVariableValue
 * composer declares 0 to 2, and the wired menu's inspection tab and variable management window
 * send each one from its own button.
 */
@RequiredArgsConstructor
public enum WiredVariableAction implements IntEnum {
    // Typing a new value in the value column (onCellEdit).
    SET_VALUE(0),
    // The create button of the add variable bubble (onCreateVariableClicked, create_var_btn).
    CREATE(1),
    // The delete button (onDeleteVariableClicked, delete_var_btn). The client sends value 0.
    DELETE(2);

    @Getter
    @JsonValue
    private final int value;

    public static WiredVariableAction fromValue(int value) {
        for (WiredVariableAction action : values()) {
            if (action.value == value) {
                return action;
            }
        }
        return null;
    }
}
