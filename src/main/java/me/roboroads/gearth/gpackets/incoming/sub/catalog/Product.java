package me.roboroads.gearth.gpackets.incoming.sub.catalog;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.roboroads.gearth.gpackets.model.enums.ProductType;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.function.UnaryOperator;

@CheckedAgainst("WIN63-202609091217-117204808")
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
    public static final Schema<Product> SCHEMA;

    static {
        UnaryOperator<Schema<FurniProduct>> furni = s -> s
                .integer("furniClassId")
                .string("extraParam")
                .integer("productCount")
                .bool("uniqueLimitedItem")
                .when("uniqueLimitedItem", true, u -> u
                        .integer("uniqueLimitedItemSeriesSize")
                        .integer("uniqueLimitedItemsLeft"));
        SCHEMA = Schema.of(Product.class)
                .enumString("productType", ProductType.class)
                .branch("productType", cases -> cases
                        .on(ProductType.BADGE, BadgeProduct.class, s -> s.string("extraParam"))
                        .on(ProductType.ITEM, FurniProduct.class, furni)
                        .on(ProductType.STUFF, FurniProduct.class, furni)
                        .on(ProductType.EFFECT, FurniProduct.class, furni)
                        .on(ProductType.CL, FurniProduct.class, furni)
                        .on(ProductType.SUBSCRIPTION, FurniProduct.class, furni)
                        .on(ProductType.R, FurniProduct.class, furni)
                        .on(ProductType.HABBICON, FurniProduct.class, furni)
                        .on(ProductType.CHAT_STYLE, FurniProduct.class, furni));
    }

    private ProductType productType;
    private String extraParam;

    public static Product fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
