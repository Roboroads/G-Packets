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

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class CatalogIndex implements Packet, JsonSerializable {
    public static final PacketType<CatalogIndex> TYPE = new PacketType<>("CatalogIndex", HMessage.Direction.TOCLIENT, CatalogIndex::fromPacket);

    private CatalogNode root;
    private Boolean newAdditionsAvailable;
    private CatalogType catalogType;

    public static CatalogIndex fromPacket(HPacket packet) {
        return CatalogIndex.builder()
                .root(CatalogNode.fromPacket(packet))
                .newAdditionsAvailable(packet.readBoolean())
                .catalogType(CatalogType.fromCode(packet.readString()))
                .build();
    }

    public static CatalogIndex fromJson(String json) {
        return Json.parse(CatalogIndex.class, json);
    }

    @Override
    public HPacket toPacket() {
        HPacket packet = new HPacket(TYPE.header(), TYPE.direction());
        if (root != null) {
            root.appendPacket(packet);
        } else {
            new CatalogNode().appendPacket(packet);
        }
        packet.appendBoolean(newAdditionsAvailable != null && newAdditionsAvailable);
        packet.appendString(catalogType != null ? catalogType.code() : "");
        return packet;
    }
}
