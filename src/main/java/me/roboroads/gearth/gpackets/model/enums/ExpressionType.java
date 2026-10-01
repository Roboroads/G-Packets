package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * An avatar expression. Ids and names from the client's {@code AvatarExpressionEnum} and the
 * expression list in {@code com/sulake/habbo/avatar/enum/__01n.as}.
 */
@RequiredArgsConstructor
public enum ExpressionType implements IntEnum {
    NONE(0),
    WAVE(1),
    BLOW_A_KISS(2),
    LAUGH(3),
    CRY(4),
    IDLE(5),
    // The client's animation for it is called "dance".
    JUMP(6),
    RESPECT(7),
    SNOWBOARD_OLLIE(8),
    SNOWBOARD_360(9),
    RIDE_JUMP(10),
    EXPRESSION_67(67);

    @Getter
    @JsonValue
    private final int value;

    public static ExpressionType fromValue(int value) {
        for (ExpressionType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
