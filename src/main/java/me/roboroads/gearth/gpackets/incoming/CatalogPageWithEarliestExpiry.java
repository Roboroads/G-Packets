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
public class CatalogPageWithEarliestExpiry implements Packet, JsonSerializable {
    public static final PacketType<CatalogPageWithEarliestExpiry> TYPE = new PacketType<>("CatalogPageWithEarliestExpiry", HMessage.Direction.TOCLIENT, CatalogPageWithEarliestExpiry::fromPacket);

    private String pageName;
    private Integer secondsToExpiry;
    private String image;

    public static CatalogPageWithEarliestExpiry fromPacket(HPacket packet) {
        return CatalogPageWithEarliestExpiry.builder()
                .pageName(packet.readString())
                .secondsToExpiry(packet.readInteger())
                .image(packet.readString())
                .build();
    }

    public static CatalogPageWithEarliestExpiry fromJson(String json) {
        return Json.parse(CatalogPageWithEarliestExpiry.class, json);
    }

    @Override
    public HPacket toPacket() {
        HPacket packet = new HPacket(TYPE.header(), TYPE.direction());
        packet.appendString(pageName != null ? pageName : "");
        packet.appendInt(secondsToExpiry != null ? secondsToExpiry : 0);
        packet.appendString(image != null ? image : "");
        return packet;
    }
}
