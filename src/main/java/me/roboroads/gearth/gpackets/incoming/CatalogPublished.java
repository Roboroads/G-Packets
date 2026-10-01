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

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class CatalogPublished implements Packet, JsonSerializable {
    public static final PacketType<CatalogPublished> TYPE = new PacketType<>("CatalogPublished", HMessage.Direction.TOCLIENT, CatalogPublished::fromPacket);

    private Boolean instantlyRefreshCatalogue;
    // Optional tail — only present when the server also wants the client to invalidate
    // its cached furniture data.
    private String newFurniDataHash;

    public static CatalogPublished fromPacket(HPacket packet) {
        CatalogPublishedBuilder builder = CatalogPublished.builder()
                .instantlyRefreshCatalogue(packet.readBoolean());

        if (packet.isEOF() == 0) {
            builder.newFurniDataHash(packet.readString());
        }
        return builder.build();
    }

    public static CatalogPublished fromJson(String json) {
        return Json.parse(CatalogPublished.class, json);
    }

    @Override
    public HPacket toPacket() {
        HPacket packet = new HPacket(TYPE.header(), TYPE.direction());
        packet.appendBoolean(instantlyRefreshCatalogue != null && instantlyRefreshCatalogue);
        if (newFurniDataHash != null) {
            packet.appendString(newFurniDataHash);
        }
        return packet;
    }
}
