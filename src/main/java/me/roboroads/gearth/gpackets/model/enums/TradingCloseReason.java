package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.roboroads.gearth.gpackets.support.schema.OpenIntEnum;

import java.util.List;

/**
 * Why a trade closed. The client's parser declares only 1, and its handler only checks for 1: any
 * other reason closes the trade, with the text inventory.trading.info.closed ("Other user canceled
 * the trade.") when the other user closed it. {@link #of} keeps every id this list doesn't name.
 */
public final class TradingCloseReason extends OpenIntEnum {
    // inventory.trading.notification.commiterror.info: "Some of the items in the trade could no
    // longer be located. Try again, please!" (shown only when the hotel setting
    // trading.commiterror.enabled is on).
    public static final TradingCloseReason COMMIT_ERROR = new TradingCloseReason(1);

    private TradingCloseReason(int value) {
        super(value);
    }

    /** The named reason with this id, or a value that keeps it. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static TradingCloseReason of(int value) {
        return of(TradingCloseReason.class, value, TradingCloseReason::new);
    }

    /** The named reasons, sorted by id. */
    public static List<TradingCloseReason> values() {
        return values(TradingCloseReason.class);
    }
}
