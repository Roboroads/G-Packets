package me.roboroads.gearth.gpackets.incoming.sub.catalog;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.roboroads.gearth.gpackets.model.enums.FrontPageItemType;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PageLinkFrontPageItem.class, name = "0"),
        @JsonSubTypes.Type(value = ProductOfferFrontPageItem.class, name = "1"),
        @JsonSubTypes.Type(value = ProductCodeFrontPageItem.class, name = "2")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class FrontPageItem implements SubPacket, JsonSerializable {
    private Integer position;
    private String itemName;
    private String itemPromoImage;
    private FrontPageItemType type;
    // 0 = never expires; otherwise seconds from "now".
    private Integer secondsToExpiration;

    public static FrontPageItem fromPacket(HPacket packet) {
        Integer position = packet.readInteger();
        String itemName = packet.readString();
        String itemPromoImage = packet.readString();
        FrontPageItemType type = FrontPageItemType.fromValue(packet.readInteger());

        if (type == null) {
            throw new IllegalArgumentException("Unknown front page item type");
        }

        switch (type) {
            case PAGE_LINK: {
                String cataloguePageLocation = packet.readString();
                Integer secondsToExpiration = packet.readInteger();
                return new PageLinkFrontPageItem(position, itemName, itemPromoImage, type, secondsToExpiration, cataloguePageLocation);
            }
            case PRODUCT_OFFER: {
                Integer productOfferId = packet.readInteger();
                Integer secondsToExpiration = packet.readInteger();
                return new ProductOfferFrontPageItem(position, itemName, itemPromoImage, type, secondsToExpiration, productOfferId);
            }
            case PRODUCT_CODE: {
                String productCode = packet.readString();
                Integer secondsToExpiration = packet.readInteger();
                return new ProductCodeFrontPageItem(position, itemName, itemPromoImage, type, secondsToExpiration, productCode);
            }
            default:
                throw new IllegalArgumentException("Unknown front page item type: " + type);
        }
    }

    @Override
    public void appendPacket(HPacket packet) {
        packet.appendInt(position != null ? position : 0);
        packet.appendString(itemName != null ? itemName : "");
        packet.appendString(itemPromoImage != null ? itemPromoImage : "");
        packet.appendInt(type != null ? type.value() : -1);
    }

    protected void appendTrailingExpiration(HPacket packet) {
        packet.appendInt(secondsToExpiration != null ? secondsToExpiration : 0);
    }
}
