package me.roboroads.gearth.gpackets.incoming.sub.catalog;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.Utils;

import java.util.List;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Offer implements SubPacket, JsonSerializable {
    private Integer offerId;
    private String localizationId;
    private Boolean isRent;
    private Integer priceInCredits;
    private Integer priceInActivityPoints;
    // Raw int from the wire; see ActivityPointType for the mapping.
    private Integer activityPointType;
    // Client field name preserved; may also be priceInSeasonCurrency.
    private Integer priceInSilver;
    private Boolean giftable;
    private List<Product> products;
    // Raw int from the wire; see ClubLevel for the mapping.
    private Integer clubLevel;
    private Boolean bundlePurchaseAllowed;
    // The client stores it but never exposes or reads it; G-Rust calls it "_unused".
    // Possibly an "is pet" flag (other emulators), unverified.
    private Boolean unknownBoolean12;
    private String previewImage;

    public static Offer fromPacket(HPacket packet) {
        return Offer.builder()
                .offerId(packet.readInteger())
                .localizationId(packet.readString())
                .isRent(packet.readBoolean())
                .priceInCredits(packet.readInteger())
                .priceInActivityPoints(packet.readInteger())
                .activityPointType(packet.readInteger())
                .priceInSilver(packet.readInteger())
                .giftable(packet.readBoolean())
                .products(Utils.readList(packet, Product::fromPacket))
                .clubLevel(packet.readInteger())
                .bundlePurchaseAllowed(packet.readBoolean())
                .unknownBoolean12(packet.readBoolean())
                .previewImage(packet.readString())
                .build();
    }

    @Override
    public void appendPacket(HPacket packet) {
        packet.appendInt(offerId != null ? offerId : 0);
        packet.appendString(localizationId != null ? localizationId : "");
        packet.appendBoolean(isRent != null && isRent);
        packet.appendInt(priceInCredits != null ? priceInCredits : 0);
        packet.appendInt(priceInActivityPoints != null ? priceInActivityPoints : 0);
        packet.appendInt(activityPointType != null ? activityPointType : 0);
        packet.appendInt(priceInSilver != null ? priceInSilver : 0);
        packet.appendBoolean(giftable != null && giftable);
        packet.appendInt(products != null ? products.size() : 0);
        if (products != null) {
            for (Product product : products) {
                product.appendPacket(packet);
            }
        }
        packet.appendInt(clubLevel != null ? clubLevel : 0);
        packet.appendBoolean(bundlePurchaseAllowed != null && bundlePurchaseAllowed);
        packet.appendBoolean(unknownBoolean12 != null && unknownBoolean12);
        packet.appendString(previewImage != null ? previewImage : "");
    }
}
