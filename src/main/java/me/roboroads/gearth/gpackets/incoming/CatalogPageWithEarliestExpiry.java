package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.Schema;

@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class CatalogPageWithEarliestExpiry implements Packet, JsonSerializable {
    public static final PacketType<CatalogPageWithEarliestExpiry> TYPE = PacketType.of("CatalogPageWithEarliestExpiry", HMessage.Direction.TOCLIENT, Schema.of(CatalogPageWithEarliestExpiry.class)
            .string("pageName")
            .integer("secondsToExpiry")
            .string("image"));

    private String pageName;
    private Integer secondsToExpiry;
    // The client's ExpiringCatalogPageWidget stores it in a field and nothing reads it;
    // the teaser image is built from pageName instead (reception/catalog_teaser_<pageName>.png).
    @Unused("The client stores it but never reads it")
    @Deprecated
    private String image;

    public static CatalogPageWithEarliestExpiry fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static CatalogPageWithEarliestExpiry fromJson(String json) {
        return Json.parse(CatalogPageWithEarliestExpiry.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
