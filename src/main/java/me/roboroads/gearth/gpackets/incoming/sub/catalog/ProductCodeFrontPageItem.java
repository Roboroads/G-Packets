package me.roboroads.gearth.gpackets.incoming.sub.catalog;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.FrontPageItemType;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;

@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class ProductCodeFrontPageItem extends FrontPageItem {
    private String productCode;

    public ProductCodeFrontPageItem(Integer position, String itemName, String itemPromoImage, FrontPageItemType type, Integer secondsToExpiry, String productCode) {
        super(position, itemName, itemPromoImage, type, secondsToExpiry);
        this.productCode = productCode;
    }

    public static ProductCodeFrontPageItem fromJson(String json) {
        return Json.parse(ProductCodeFrontPageItem.class, json);
    }
}
