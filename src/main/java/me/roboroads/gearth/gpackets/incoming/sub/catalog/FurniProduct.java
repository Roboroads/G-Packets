package me.roboroads.gearth.gpackets.incoming.sub.catalog;

import gearth.protocol.HPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.ProductType;
import me.roboroads.gearth.gpackets.support.Json;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class FurniProduct extends Product {
    private Integer furniClassId;
    private Integer productCount;
    private Boolean uniqueLimitedItem;
    private Integer uniqueLimitedItemSeriesSize;
    private Integer uniqueLimitedItemsLeft;

    public static FurniProduct fromPacket(HPacket packet, ProductType productType) {
        FurniProductBuilder<?, ?> builder = FurniProduct.builder()
                .productType(productType)
                .furniClassId(packet.readInteger())
                .extraParam(packet.readString())
                .productCount(packet.readInteger());

        boolean uniqueLimitedItem = packet.readBoolean();
        builder.uniqueLimitedItem(uniqueLimitedItem);
        if (uniqueLimitedItem) {
            builder.uniqueLimitedItemSeriesSize(packet.readInteger());
            builder.uniqueLimitedItemsLeft(packet.readInteger());
        }
        return builder.build();
    }

    public static FurniProduct fromJson(String json) {
        return Json.parse(FurniProduct.class, json);
    }

    @Override
    public void appendPacket(HPacket packet) {
        super.appendPacket(packet);
        packet.appendInt(furniClassId != null ? furniClassId : 0);
        packet.appendString(extraParam() != null ? extraParam() : "");
        packet.appendInt(productCount != null ? productCount : 0);
        packet.appendBoolean(uniqueLimitedItem != null && uniqueLimitedItem);
        if (uniqueLimitedItem != null && uniqueLimitedItem) {
            packet.appendInt(uniqueLimitedItemSeriesSize != null ? uniqueLimitedItemSeriesSize : 0);
            packet.appendInt(uniqueLimitedItemsLeft != null ? uniqueLimitedItemsLeft : 0);
        }
    }
}
