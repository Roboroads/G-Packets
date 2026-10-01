package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.roboroads.gearth.gpackets.support.schema.OpenIntEnum;

import java.util.List;

/**
 * A purse currency. Values and names come from the client's purse constants
 * (com.sulake.habbo.catalog.purse), which list every type the client knows. {@link #of} keeps an id
 * this list doesn't have, so a newer currency survives a read and a write.
 */
public final class ActivityPointType extends OpenIntEnum {
    public static final ActivityPointType DUCKETS = new ActivityPointType(0);
    public static final ActivityPointType NO_OP_1 = new ActivityPointType(1);
    public static final ActivityPointType NO_OP_2 = new ActivityPointType(2);
    // Client constant is obfuscated and has no descriptive usage, and the hotel's
    // activitypoint.name.* variables don't name it. Old texts call type 3 "gift points".
    public static final ActivityPointType UNKNOWN_3 = new ActivityPointType(3);
    public static final ActivityPointType NO_OP_4 = new ActivityPointType(4);
    // Client constant is obfuscated; its icon style is gated by the "diamonds.enabled" property.
    public static final ActivityPointType DIAMONDS = new ActivityPointType(5);
    public static final ActivityPointType CREDITS = new ActivityPointType(7);
    public static final ActivityPointType SEASONAL_1 = new ActivityPointType(101);
    public static final ActivityPointType SEASONAL_2 = new ActivityPointType(102);
    public static final ActivityPointType SEASONAL_3 = new ActivityPointType(103);
    public static final ActivityPointType SEASONAL_4 = new ActivityPointType(104);
    public static final ActivityPointType SEASONAL_5 = new ActivityPointType(105);
    public static final ActivityPointType SILVER = new ActivityPointType(1000);
    public static final ActivityPointType EMERALD = new ActivityPointType(1001);

    private ActivityPointType(int value) {
        super(value);
    }

    /** The named currency with this id, or a value that keeps it. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ActivityPointType of(int value) {
        return of(ActivityPointType.class, value, ActivityPointType::new);
    }

    /** The named currencies, sorted by id. */
    public static List<ActivityPointType> values() {
        return values(ActivityPointType.class);
    }
}
