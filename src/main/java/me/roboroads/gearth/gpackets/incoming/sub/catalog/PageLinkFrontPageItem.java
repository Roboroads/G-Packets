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
public class PageLinkFrontPageItem extends FrontPageItem {
    private String cataloguePageLocation;

    public PageLinkFrontPageItem(Integer position, String itemName, String itemPromoImage, FrontPageItemType type, Integer secondsToExpiration, String cataloguePageLocation) {
        super(position, itemName, itemPromoImage, type, secondsToExpiration);
        this.cataloguePageLocation = cataloguePageLocation;
    }

    public static PageLinkFrontPageItem fromJson(String json) {
        return Json.parse(PageLinkFrontPageItem.class, json);
    }

    @Override
    public void appendPacket(HPacket packet) {
        super.appendPacket(packet);
        packet.appendString(cataloguePageLocation != null ? cataloguePageLocation : "");
        appendTrailingExpiration(packet);
    }
}
