package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * Whether a furni in the room is a wall item or a floor item, as PickupObject sends it. The client's
 * PickupObject composer turns the room object category into this code (a floor item,
 * RoomObjectCategoryEnum.OBJECT_CATEGORY_FURNITURE = 10, becomes 2; a wall item, 20, becomes 1) and
 * sends nothing for any other category.
 */
@RequiredArgsConstructor
public enum FurniPlacementType implements IntEnum {
    WALL(1),
    FLOOR(2);

    @Getter
    @JsonValue
    private final int value;

    public static FurniPlacementType fromValue(int value) {
        for (FurniPlacementType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
