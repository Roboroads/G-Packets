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
public class ProductCodeFrontPageItem extends FrontPageItem {
    private String productCode;

    public ProductCodeFrontPageItem(Integer position, String itemName, String itemPromoImage, FrontPageItemType type, Integer secondsToExpiration, String productCode) {
        super(position, itemName, itemPromoImage, type, secondsToExpiration);
        this.productCode = productCode;
    }

    public static ProductCodeFrontPageItem fromJson(String json) {
        return Json.parse(ProductCodeFrontPageItem.class, json);
    }

    @Override
    public void appendPacket(HPacket packet) {
        super.appendPacket(packet);
        packet.appendString(productCode != null ? productCode : "");
        appendTrailingExpiration(packet);
    }
}
