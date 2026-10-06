package me.roboroads.gearth.gpackets.incoming.sub.catalog;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class CatalogNode implements SubPacket, JsonSerializable {
    public static final Schema<CatalogNode> SCHEMA = Schema.of(CatalogNode.class)
            .bool("visible")
            .integer("icon")
            .integer("pageId")
            .string("pageName")
            .string("pageTitle")
            .list("offerIds", WireType.INT)
            .list("children", () -> CatalogNode.SCHEMA);

    private Boolean visible;
    private Integer icon;
    private Integer pageId;
    private String pageName;
    // The page's display title. The client calls it localization, but it's a plain string, unlike
    // CatalogPage.localization.
    private String pageTitle;
    private List<Integer> offerIds;
    private List<CatalogNode> children;

    public static CatalogNode fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
