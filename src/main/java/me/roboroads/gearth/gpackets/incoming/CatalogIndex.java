package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.catalog.CatalogNode;
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
public class CatalogIndex implements Packet, JsonSerializable {
    public static final PacketType<CatalogIndex> TYPE = PacketType.of("CatalogIndex", HMessage.Direction.TOCLIENT, Schema.of(CatalogIndex.class)
            .struct("root", CatalogNode.SCHEMA)
            .bool("newAdditionsAvailable")
            .enumString("catalogType", CatalogType.class));

    private CatalogNode root;
    private Boolean newAdditionsAvailable;
    private CatalogType catalogType;

    public static CatalogIndex fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static CatalogIndex fromJson(String json) {
        return Json.parse(CatalogIndex.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
