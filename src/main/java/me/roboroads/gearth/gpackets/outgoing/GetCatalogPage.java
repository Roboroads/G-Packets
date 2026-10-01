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
import me.roboroads.gearth.gpackets.support.schema.Schema;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class GetCatalogPage implements Packet, JsonSerializable {
    public static final PacketType<GetCatalogPage> TYPE = PacketType.of("GetCatalogPage", HMessage.Direction.TOSERVER, Schema.of(GetCatalogPage.class)
            .integer("pageId")
            .integer("offerId")
            .enumString("catalogType", CatalogType.class));

    private Integer pageId;
    @Builder.Default
    private Integer offerId = -1;
    private CatalogType catalogType;

    public static GetCatalogPage fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static GetCatalogPage fromJson(String json) {
        return Json.parse(GetCatalogPage.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
