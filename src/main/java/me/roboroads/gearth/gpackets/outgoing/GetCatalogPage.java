package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.CatalogType;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class GetCatalogPage implements Packet, JsonSerializable {
    public static final PacketType<GetCatalogPage> TYPE = new PacketType<>("GetCatalogPage", HMessage.Direction.TOSERVER, GetCatalogPage::fromPacket);

    private Integer pageId;
    @Builder.Default
    private Integer offerId = -1;
    private CatalogType catalogType;

    public static GetCatalogPage fromPacket(HPacket packet) {
        return GetCatalogPage.builder()
                .pageId(packet.readInteger())
                .offerId(packet.readInteger())
                .catalogType(CatalogType.fromCode(packet.readString()))
                .build();
    }

    public static GetCatalogPage fromJson(String json) {
        return Json.parse(GetCatalogPage.class, json);
    }

    @Override
    public HPacket toPacket() {
        HPacket packet = new HPacket(TYPE.header(), TYPE.direction());
        packet.appendInt(pageId != null ? pageId : 0);
        packet.appendInt(offerId != null ? offerId : -1);
        packet.appendString(catalogType != null ? catalogType.code() : "");
        return packet;
    }
}
