package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * A dance. The client's menus send 0 to 4 (OwnAvatarMenuView, MeMenuDanceView); 2 to 4 are only in
 * the Habbo Club dance menu ("Join HC to get more dance moves!").
 */
@RequiredArgsConstructor
public enum DanceStyle implements IntEnum {
    // widget.memenu.dance.stop: "Stop Dancing"
    NONE(0),
    // widget.memenu.dance1: "Dance"
    DANCE(1),
    // widget.memenu.dance2: "Pogo Mogo"
    POGO_MOGO(2),
    // widget.memenu.dance3: "Duck Funk"
    DUCK_FUNK(3),
    // widget.memenu.dance4: "The Rollie"
    THE_ROLLIE(4);

    @Getter
    @JsonValue
    private final int value;

    public static DanceStyle fromValue(int value) {
        for (DanceStyle style : values()) {
            if (style.value == value) {
                return style;
            }
        }
        return null;
    }
}
