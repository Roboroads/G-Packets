package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;

@Data
@NoArgsConstructor
public class GetCatalogPageWithEarliestExpiry implements Packet, JsonSerializable {
    public static final PacketType<GetCatalogPageWithEarliestExpiry> TYPE = new PacketType<>("GetCatalogPageWithEarliestExpiry", HMessage.Direction.TOSERVER, GetCatalogPageWithEarliestExpiry::fromPacket);

    public static GetCatalogPageWithEarliestExpiry fromPacket(HPacket packet) {
        return new GetCatalogPageWithEarliestExpiry();
    }

    public static GetCatalogPageWithEarliestExpiry fromJson(String json) {
        return Json.parse(GetCatalogPageWithEarliestExpiry.class, json);
    }

    @Override
    public HPacket toPacket() {
        return new HPacket(TYPE.header(), TYPE.direction());
    }
}
