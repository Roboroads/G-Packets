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
public class ProductOfferFrontPageItem extends FrontPageItem {
    private Integer productOfferId;

    public ProductOfferFrontPageItem(Integer position, String itemName, String itemPromoImage, FrontPageItemType type, Integer secondsToExpiry, Integer productOfferId) {
        super(position, itemName, itemPromoImage, type, secondsToExpiry);
        this.productOfferId = productOfferId;
    }

    public static ProductOfferFrontPageItem fromJson(String json) {
        return Json.parse(ProductOfferFrontPageItem.class, json);
    }
}
