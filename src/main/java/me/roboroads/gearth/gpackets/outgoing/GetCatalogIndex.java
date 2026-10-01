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
public class GetCatalogIndex implements Packet, JsonSerializable {
    public static final PacketType<GetCatalogIndex> TYPE = new PacketType<>("GetCatalogIndex", HMessage.Direction.TOSERVER, GetCatalogIndex::fromPacket);

    private CatalogType catalogType;

    public static GetCatalogIndex fromPacket(HPacket packet) {
        return GetCatalogIndex.builder()
                .catalogType(CatalogType.fromCode(packet.readString()))
                .build();
    }

    public static GetCatalogIndex fromJson(String json) {
        return Json.parse(GetCatalogIndex.class, json);
    }

    @Override
    public HPacket toPacket() {
        HPacket packet = new HPacket(TYPE.header(), TYPE.direction());
        packet.appendString(catalogType != null ? catalogType.code() : "");
        return packet;
    }
}
