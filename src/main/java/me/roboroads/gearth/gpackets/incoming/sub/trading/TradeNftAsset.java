package me.roboroads.gearth.gpackets.incoming.sub.trading;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

/**
 * One NFT asset you can trade or that is in a trade: its asset id, then the collectible's product
 * info, which the client reads with the same class as its other collectibles
 * (communication/messages/parser/collectibles).
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class TradeNftAsset implements SubPacket, JsonSerializable {
    public static final Schema<TradeNftAsset> SCHEMA = Schema.of(TradeNftAsset.class)
            .longValue("assetId")
            .shortValue("productTypeId")
            .string("itemTypeId")
            .integer("score")
            .string("petFigureString")
            .list("figureSetIds", WireType.INT)
            .string("productCode")
            .string("rarity");

    // AddNftToTrade and RemoveNftFromTrade send it back as an int.
    private Long assetId;
    // The collectibles catalog names -1 unknown, 0 wall, 1 room (floor), 2 effect, 4 badge, 9 chat
    // style, 10 pets and 11 clothing (product.type.* texts, CollectiblesController.getProductType).
    private Short productTypeId;
    // The furni, effect or other item type, as a string; for furni the client parses it as the
    // furni type id.
    private String itemTypeId;
    private Integer score;
    private String petFigureString;
    private List<Integer> figureSetIds;
    // The client groups trade assets by it.
    private String productCode;
    private String rarity;

    public static TradeNftAsset fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
