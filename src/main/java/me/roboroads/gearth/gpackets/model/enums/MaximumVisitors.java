package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * The maximum number of visitors you can save for a room. The client's room settings dropdown
 * ({@code RoomSettingsCtrl.refreshMaxVisitors}) offers 10 to 50 in steps of 5, and up to 75 for
 * VIP users, and saving sends the selected entry.
 */
@RequiredArgsConstructor
public enum MaximumVisitors implements IntEnum {
    VISITORS_10(10),
    VISITORS_15(15),
    VISITORS_20(20),
    VISITORS_25(25),
    VISITORS_30(30),
    VISITORS_35(35),
    VISITORS_40(40),
    VISITORS_45(45),
    VISITORS_50(50),
    // The client only offers 55 to 75 to VIP users.
    VISITORS_55(55),
    VISITORS_60(60),
    VISITORS_65(65),
    VISITORS_70(70),
    VISITORS_75(75);

    @Getter
    @JsonValue
    private final int value;
}
