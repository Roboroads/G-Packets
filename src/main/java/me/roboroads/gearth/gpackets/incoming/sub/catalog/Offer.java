package me.roboroads.gearth.gpackets.incoming.sub.catalog;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Offer implements SubPacket, JsonSerializable {
    public static final Schema<Offer> SCHEMA = Schema.of(Offer.class)
            .integer("offerId")
            .string("localizationId")
            .bool("isRent")
            .integer("priceInCredits")
            .integer("priceInActivityPoints")
            .integer("activityPointType")
            .integer("priceInSilver")
            .bool("giftable")
            .list("products", Product.SCHEMA)
            .integer("clubLevel")
            .bool("bundlePurchaseAllowed")
            .bool("unknownBoolean12")
            .string("previewImage");

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
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
