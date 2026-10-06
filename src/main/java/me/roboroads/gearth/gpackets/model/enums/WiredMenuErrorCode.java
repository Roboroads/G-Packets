package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.roboroads.gearth.gpackets.support.schema.OpenIntEnum;

import java.util.List;

/**
 * Why the server refused a wired menu request. The client's parser declares 0 to 7, but only 7 has
 * a text and a notification (WiredMenuController shows ${wiredmenu.error_message.&lt;code&gt;} for it).
 * The inspection tab also reacts to 0: while it waits for an object's variables, it drops what it was
 * loading. 1 to 6 have no text and nothing acts on them, so {@link #of} keeps them and any other id
 * this list doesn't name.
 */
public final class WiredMenuErrorCode extends OpenIntEnum {
    // wiredmenu.error_message.7: "Only the owner of this variable can perform this action."
    public static final WiredMenuErrorCode NOT_VARIABLE_OWNER = new WiredMenuErrorCode(7);

    private WiredMenuErrorCode(int value) {
        super(value);
    }

    /** The named code with this id, or a value that keeps it. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static WiredMenuErrorCode of(int value) {
        return of(WiredMenuErrorCode.class, value, WiredMenuErrorCode::new);
    }

    /** The named codes, sorted by id. */
    public static List<WiredMenuErrorCode> values() {
        return values(WiredMenuErrorCode.class);
    }
}
