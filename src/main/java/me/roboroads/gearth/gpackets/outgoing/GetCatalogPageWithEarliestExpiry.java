package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

@Data
@NoArgsConstructor
public class GetCatalogPageWithEarliestExpiry implements Packet, JsonSerializable {
    public static final PacketType<GetCatalogPageWithEarliestExpiry> TYPE = PacketType.of("GetCatalogPageWithEarliestExpiry", HMessage.Direction.TOSERVER,
            Schema.of(GetCatalogPageWithEarliestExpiry.class));

    public static GetCatalogPageWithEarliestExpiry fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static GetCatalogPageWithEarliestExpiry fromJson(String json) {
        return Json.parse(GetCatalogPageWithEarliestExpiry.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
