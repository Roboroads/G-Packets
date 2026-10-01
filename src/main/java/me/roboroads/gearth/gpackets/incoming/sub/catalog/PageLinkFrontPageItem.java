package me.roboroads.gearth.gpackets.incoming.sub.catalog;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.FrontPageItemType;
import me.roboroads.gearth.gpackets.support.Json;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class PageLinkFrontPageItem extends FrontPageItem {
    // The client calls it cataloguePageLocation.
    private String catalogPageLocation;

    public PageLinkFrontPageItem(Integer position, String itemName, String itemPromoImage, FrontPageItemType type, Integer secondsToExpiry, String catalogPageLocation) {
        super(position, itemName, itemPromoImage, type, secondsToExpiry);
        this.catalogPageLocation = catalogPageLocation;
    }

    public static PageLinkFrontPageItem fromJson(String json) {
        return Json.parse(PageLinkFrontPageItem.class, json);
    }
}
