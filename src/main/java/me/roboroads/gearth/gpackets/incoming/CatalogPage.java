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
import me.roboroads.gearth.gpackets.support.Utils;

import java.util.List;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class CatalogPage implements Packet, JsonSerializable {
    public static final PacketType<CatalogPage> TYPE = new PacketType<>("CatalogPage", HMessage.Direction.TOCLIENT, CatalogPage::fromPacket);

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
        CatalogPageBuilder builder = CatalogPage.builder()
                .pageId(packet.readInteger())
                .catalogType(CatalogType.fromCode(packet.readString()))
                .layoutCode(packet.readString())
                .localization(Localization.fromPacket(packet))
                .offers(Utils.readList(packet, Offer::fromPacket))
                .offerId(packet.readInteger())
                .acceptSeasonCurrencyAsCredits(packet.readBoolean());

        if (packet.isEOF() == 0) {
            builder.frontPageItems(Utils.readList(packet, FrontPageItem::fromPacket));
        }
        return builder.build();
    }

    public static CatalogPage fromJson(String json) {
        return Json.parse(CatalogPage.class, json);
    }

    @Override
    public HPacket toPacket() {
        HPacket packet = new HPacket(TYPE.header(), TYPE.direction());
        packet.appendInt(pageId != null ? pageId : 0);
        packet.appendString(catalogType != null ? catalogType.code() : "");
        packet.appendString(layoutCode != null ? layoutCode : "");
        if (localization != null) {
            localization.appendPacket(packet);
        } else {
            new Localization().appendPacket(packet);
        }
        packet.appendInt(offers != null ? offers.size() : 0);
        if (offers != null) {
            for (Offer offer : offers) {
                offer.appendPacket(packet);
            }
        }
        packet.appendInt(offerId != null ? offerId : -1);
        packet.appendBoolean(acceptSeasonCurrencyAsCredits != null && acceptSeasonCurrencyAsCredits);
        if (frontPageItems != null) {
            packet.appendInt(frontPageItems.size());
            for (FrontPageItem item : frontPageItems) {
                item.appendPacket(packet);
            }
        }
        return packet;
    }
}
