package me.roboroads.gearth.gpackets.incoming.sub.catalog;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.ActivityPointType;
import me.roboroads.gearth.gpackets.model.enums.ClubLevel;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.Unused;
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
            .enumInt("activityPointType", ActivityPointType.class)
            .integer("priceInSilver")
            .bool("giftable")
            .list("products", Product.SCHEMA)
            .enumInt("clubLevel", ClubLevel.class)
            .bool("bundlePurchaseAllowed")
            .bool("unknownBoolean12")
            .string("previewImage");

    private Integer offerId;
    private String localizationId;
    private Boolean isRent;
    private Integer priceInCredits;
    private Integer priceInActivityPoints;
    private ActivityPointType activityPointType;
    // The price in silver (ActivityPointType.SILVER); the client's catalog utils show it as "silver".
    private Integer priceInSilver;
    private Boolean giftable;
    private List<Product> products;
    private ClubLevel clubLevel;
    private Boolean bundlePurchaseAllowed;
    // The client stores it but never exposes or reads it; G-Rust calls it "_unused".
    // Possibly an "is pet" flag (other emulators), unverified.
    @Unused("The client stores it but never reads it")
    @Deprecated
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
