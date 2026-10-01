package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/** Who may trade in a room: the index into the room settings trade dropdown. */
@RequiredArgsConstructor
public enum TradeMode implements IntEnum {
    // navigator.roomsettings.trade_not_allowed: "Trading not allowed"
    NOT_ALLOWED(0),
    // navigator.roomsettings.trade_not_with_Controller: "Room owners and users with rights"
    OWNERS_AND_RIGHTS(1),
    // navigator.roomsettings.trade_allowed: "Everyone can trade"
    EVERYONE(2);

    @Getter
    @JsonValue
    private final int value;
}
