package me.roboroads.gearth.gpackets.incoming.sub.catalog;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.Utils;

import java.util.List;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class CatalogNode implements SubPacket, JsonSerializable {
    private Boolean visible;
    private Integer icon;
    private Integer pageId;
    private String pageName;
    private String localization;
    private List<Integer> offerIds;
    private List<CatalogNode> children;

    public static CatalogNode fromPacket(HPacket packet) {
        return CatalogNode.builder()
                .visible(packet.readBoolean())
                .icon(packet.readInteger())
                .pageId(packet.readInteger())
                .pageName(packet.readString())
                .localization(packet.readString())
                .offerIds(Utils.readList(packet, HPacket::readInteger))
                .children(Utils.readList(packet, CatalogNode::fromPacket))
                .build();
    }

    @Override
    public void appendPacket(HPacket packet) {
        packet.appendBoolean(visible != null && visible);
        packet.appendInt(icon != null ? icon : 0);
        packet.appendInt(pageId != null ? pageId : 0);
        packet.appendString(pageName != null ? pageName : "");
        packet.appendString(localization != null ? localization : "");
        packet.appendInt(offerIds != null ? offerIds.size() : 0);
        if (offerIds != null) {
            for (Integer id : offerIds) {
                packet.appendInt(id != null ? id : 0);
            }
        }
        packet.appendInt(children != null ? children.size() : 0);
        if (children != null) {
            for (CatalogNode child : children) {
                child.appendPacket(packet);
            }
        }
    }
}