package me.roboroads.gearth.gpackets.incoming.sub.catalog;

import gearth.protocol.HPacket;
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
public class ProductOfferFrontPageItem extends FrontPageItem {
    private Integer productOfferId;

    public ProductOfferFrontPageItem(Integer position, String itemName, String itemPromoImage, FrontPageItemType type, Integer secondsToExpiration, Integer productOfferId) {
        super(position, itemName, itemPromoImage, type, secondsToExpiration);
        this.productOfferId = productOfferId;
    }

    public static ProductOfferFrontPageItem fromJson(String json) {
        return Json.parse(ProductOfferFrontPageItem.class, json);
    }

    @Override
    public void appendPacket(HPacket packet) {
        super.appendPacket(packet);
        packet.appendInt(productOfferId != null ? productOfferId : 0);
        appendTrailingExpiration(packet);
    }
}
