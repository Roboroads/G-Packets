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
public class GetCatalogIndex implements Packet, JsonSerializable {
    public static final PacketType<GetCatalogIndex> TYPE = PacketType.of("GetCatalogIndex", HMessage.Direction.TOSERVER, Schema.of(GetCatalogIndex.class)
            .enumString("catalogType", CatalogType.class));

    private CatalogType catalogType;

    public static GetCatalogIndex fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static GetCatalogIndex fromJson(String json) {
        return Json.parse(GetCatalogIndex.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
