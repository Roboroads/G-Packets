package me.roboroads.gearth.gpackets.incoming.sub.catalog;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;

@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class FurniProduct extends Product {
    private Integer furniClassId;
    private Integer productCount;
    private Boolean uniqueLimitedItem;
    private Integer uniqueLimitedItemSeriesSize;
    private Integer uniqueLimitedItemsLeft;

    public static FurniProduct fromJson(String json) {
        return Json.parse(FurniProduct.class, json);
    }
}
