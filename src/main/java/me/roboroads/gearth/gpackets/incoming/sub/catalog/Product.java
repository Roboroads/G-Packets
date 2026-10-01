package me.roboroads.gearth.gpackets.incoming.sub.catalog;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.roboroads.gearth.gpackets.model.enums.ProductType;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "productType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FurniProduct.class, names = {"i", "s", "e", "cl", "h", "r", "habbicon", "chat_style"}),
        @JsonSubTypes.Type(value = BadgeProduct.class, name = "b")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Product implements SubPacket, JsonSerializable {
    private ProductType productType;
    private String extraParam;

    public static Product fromPacket(HPacket packet) {
        ProductType productType = ProductType.fromCode(packet.readString());

        if (productType == null) {
            throw new IllegalArgumentException("Unknown product type");
        }

        if (productType == ProductType.BADGE) {
            return BadgeProduct.fromPacket(packet, productType);
        }
        return FurniProduct.fromPacket(packet, productType);
    }

    @Override
    public void appendPacket(HPacket packet) {
        packet.appendString(productType != null ? productType.code() : "");
    }
}
