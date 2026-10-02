package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.roboroads.gearth.gpackets.support.schema.OpenIntEnum;

import java.util.List;

/**
 * Why a trade couldn't start. The client shows the text inventory.trading.openfail.&lt;reason&gt; for
 * any id, except 7 and 8, which both show inventory.trading.info.already_open ("You are already
 * trading with someone so you cannot start a new trade."). The texts name 1 to 4 and 6 to 8, so
 * {@link #of} keeps 5 and any other id this list doesn't name.
 */
public final class TradeOpenFailedReason extends OpenIntEnum {
    // inventory.trading.openfail.1: trading isn't allowed in the hotel right now.
    public static final TradeOpenFailedReason HOTEL_TRADING_DISABLED = new TradeOpenFailedReason(1);
    // inventory.trading.openfail.2: trading is turned off for your account.
    public static final TradeOpenFailedReason YOUR_TRADING_DISABLED = new TradeOpenFailedReason(2);
    // inventory.trading.openfail.3: "You can't trade because you have a trade lock."
    public static final TradeOpenFailedReason TRADE_LOCKED = new TradeOpenFailedReason(3);
    // inventory.trading.openfail.4: "%otherusername% doesn't want to / cannot trade atm."
    public static final TradeOpenFailedReason OTHER_USER_CANNOT_TRADE = new TradeOpenFailedReason(4);
    // inventory.trading.openfail.6: "Trading is not allowed in this room."
    public static final TradeOpenFailedReason ROOM_TRADING_DISABLED = new TradeOpenFailedReason(6);
    // inventory.trading.openfail.7: "You already have an ongoing trade. You must finish that trade
    // before you can open another trade."
    public static final TradeOpenFailedReason YOU_ARE_ALREADY_TRADING = new TradeOpenFailedReason(7);
    // inventory.trading.openfail.8: "%otherusername% is already trading."
    public static final TradeOpenFailedReason OTHER_USER_ALREADY_TRADING = new TradeOpenFailedReason(8);

    private TradeOpenFailedReason(int value) {
        super(value);
    }

    /** The named reason with this id, or a value that keeps it. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static TradeOpenFailedReason of(int value) {
        return of(TradeOpenFailedReason.class, value, TradeOpenFailedReason::new);
    }

    /** The named reasons, sorted by id. */
    public static List<TradeOpenFailedReason> values() {
        return values(TradeOpenFailedReason.class);
    }
}
