package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.roboroads.gearth.gpackets.support.schema.OpenIntEnum;

import java.util.List;

/**
 * What wrote a line in the wired room logs. The logs window shows the text
 * wiredmenu.logs_overview.log_source.&lt;source&gt; for any id, so {@link #of} keeps an id this list
 * doesn't name.
 */
public final class WiredLogSource extends OpenIntEnum {
    // Only as a filter: wiredmenu.logs_overview.log_source.all ("All"), the first item of the logs
    // window's log_source_menu, which the client sends as -1.
    public static final WiredLogSource ALL = new WiredLogSource(-1);
    // wiredmenu.logs_overview.log_source.0: "System"
    public static final WiredLogSource SYSTEM = new WiredLogSource(0);
    // wiredmenu.logs_overview.log_source.1: "Wired"
    public static final WiredLogSource WIRED = new WiredLogSource(1);

    private WiredLogSource(int value) {
        super(value);
    }

    /** The named source with this id, or a value that keeps it. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static WiredLogSource of(int value) {
        return of(WiredLogSource.class, value, WiredLogSource::new);
    }

    /** The named sources, sorted by id. */
    public static List<WiredLogSource> values() {
        return values(WiredLogSource.class);
    }
}
