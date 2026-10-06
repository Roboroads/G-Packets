package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.roboroads.gearth.gpackets.support.schema.OpenIntEnum;

import java.util.List;

/**
 * The level of a line in the wired room logs. The logs window shows the text
 * wiredmenu.logs_overview.log_level.&lt;level&gt; for any id and colors 0 to 3, so {@link #of} keeps an
 * id this list doesn't name.
 */
public final class WiredLogLevel extends OpenIntEnum {
    // Only as a filter: wiredmenu.logs_overview.log_level.all ("All"), the first item of the logs
    // window's log_level_menu, which the client sends as -1.
    public static final WiredLogLevel ALL = new WiredLogLevel(-1);
    // wiredmenu.logs_overview.log_level.0: "INFO"
    public static final WiredLogLevel INFO = new WiredLogLevel(0);
    // wiredmenu.logs_overview.log_level.1: "WARN"
    public static final WiredLogLevel WARN = new WiredLogLevel(1);
    // wiredmenu.logs_overview.log_level.2: "ERROR"
    public static final WiredLogLevel ERROR = new WiredLogLevel(2);
    // wiredmenu.logs_overview.log_level.3: "DEBUG"
    public static final WiredLogLevel DEBUG = new WiredLogLevel(3);

    private WiredLogLevel(int value) {
        super(value);
    }

    /** The named level with this id, or a value that keeps it. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static WiredLogLevel of(int value) {
        return of(WiredLogLevel.class, value, WiredLogLevel::new);
    }

    /** The named levels, sorted by id. */
    public static List<WiredLogLevel> values() {
        return values(WiredLogLevel.class);
    }
}
