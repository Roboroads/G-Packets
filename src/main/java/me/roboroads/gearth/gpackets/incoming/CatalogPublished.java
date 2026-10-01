package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
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
public class CatalogPublished implements Packet, JsonSerializable {
    public static final PacketType<CatalogPublished> TYPE = PacketType.of("CatalogPublished", HMessage.Direction.TOCLIENT, Schema.of(CatalogPublished.class)
            .bool("instantlyRefreshCatalog")
            .optional(s -> s.string("newFurniDataHash")));

    // The client calls it instantlyRefreshCatalogue.
    private Boolean instantlyRefreshCatalog;
    // Optional tail — only present when the server also wants the client to invalidate
    // its cached furniture data.
    private String newFurniDataHash;

    public static CatalogPublished fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static CatalogPublished fromJson(String json) {
        return Json.parse(CatalogPublished.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
