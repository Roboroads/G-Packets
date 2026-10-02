package me.roboroads.gearth.gpackets.incoming.sub.trading;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.furni.StuffData;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** One furni in a trade offer. The client's __W1o/__K2h; an inventory furni has another layout. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class TradeItem implements SubPacket, JsonSerializable {
    public static final Schema<TradeItem> SCHEMA = Schema.of(TradeItem.class)
            .integer("itemId")
            .string("itemType")
            .integer("roomItemId")
            .integer("furniClassId")
            .integer("category")
            .bool("isGroupable")
            .struct("stuffData", StuffData.SCHEMA)
            .integer("creationDay")
            .integer("creationMonth")
            .integer("creationYear")
            .when("itemType", "S", s -> s.integer("extra"));

    // The inventory item id: RemoveItemFromTrade sends it (the inventory's FurnitureItem.id).
    private Integer itemId;
    // "S" for floor furni, "I" for wall furni. The client upper-cases it before it compares.
    private String itemType;
    // The furni's own id (the inventory's FurnitureItem.ref), which the inventory uses to lock the
    // furni you put in the trade.
    private Integer roomItemId;
    // The furni's type in the furni data, like FurniProduct.furniClassId. The client calls it
    // itemTypeId.
    private Integer furniClassId;
    // The furni category; the client groups posters (6) and group furni (17) by their stuff data.
    private Integer category;
    // Whether the client stacks it with others of the same type in the trade view.
    private Boolean isGroupable;
    private StuffData stuffData;
    private Integer creationDay;
    private Integer creationMonth;
    private Integer creationYear;
    // Only for floor furni ("S"). The client also reads it as songId.
    private Integer extra;

    public static TradeItem fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
