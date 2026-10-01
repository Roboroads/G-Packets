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
import me.roboroads.gearth.gpackets.support.schema.Schema;

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
    public static final Schema<FrontPageItem> SCHEMA = Schema.of(FrontPageItem.class)
            .integer("position")
            .string("itemName")
            .string("itemPromoImage")
            .enumInt("type", FrontPageItemType.class)
            .branch("type", cases -> cases
                    .on(FrontPageItemType.PAGE_LINK, PageLinkFrontPageItem.class, s -> s
                            .string("catalogPageLocation")
                            .integer("secondsToExpiry"))
                    .on(FrontPageItemType.PRODUCT_OFFER, ProductOfferFrontPageItem.class, s -> s
                            .integer("productOfferId")
                            .integer("secondsToExpiry"))
                    .on(FrontPageItemType.PRODUCT_CODE, ProductCodeFrontPageItem.class, s -> s
                            .string("productCode")
                            .integer("secondsToExpiry")));

    private Integer position;
    private String itemName;
    private String itemPromoImage;
    private FrontPageItemType type;
    // 0 = never expires; otherwise seconds from "now". The client calls it secondsToExpiration.
    private Integer secondsToExpiry;

    public static FrontPageItem fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
