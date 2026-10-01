package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.catalog.FrontPageItem;
import me.roboroads.gearth.gpackets.incoming.sub.catalog.Localization;
import me.roboroads.gearth.gpackets.incoming.sub.catalog.Offer;
import me.roboroads.gearth.gpackets.model.enums.CatalogType;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class CatalogPage implements Packet, JsonSerializable {
    public static final PacketType<CatalogPage> TYPE = PacketType.of("CatalogPage", HMessage.Direction.TOCLIENT, Schema.of(CatalogPage.class)
            .integer("pageId")
            .enumString("catalogType", CatalogType.class)
            .string("layoutCode")
            .struct("localization", Localization.SCHEMA)
            .list("offers", Offer.SCHEMA)
            .integer("offerId")
            .bool("acceptSeasonCurrencyAsCredits")
            .optional(s -> s.list("frontPageItems", FrontPageItem.SCHEMA)));

    private Integer pageId;
    private CatalogType catalogType;
    private String layoutCode;
    private Localization localization;
    private List<Offer> offers;
    // Offer to focus on the page; -1 if none.
    @Builder.Default
    private Integer offerId = -1;
    private Boolean acceptSeasonCurrencyAsCredits;
    // Optional tail — older server builds omit it.
    private List<FrontPageItem> frontPageItems;

    public static CatalogPage fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static CatalogPage fromJson(String json) {
        return Json.parse(CatalogPage.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
