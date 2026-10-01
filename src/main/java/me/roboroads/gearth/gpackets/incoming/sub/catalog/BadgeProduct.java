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
public class BadgeProduct extends Product {

    public static BadgeProduct fromPacket(HPacket packet, ProductType productType) {
        return BadgeProduct.builder()
                .productType(productType)
                .extraParam(packet.readString())
                .build();
    }

    public static BadgeProduct fromJson(String json) {
        return Json.parse(BadgeProduct.class, json);
    }

    @Override
    public void appendPacket(HPacket packet) {
        super.appendPacket(packet);
        packet.appendString(extraParam() != null ? extraParam() : "");
    }
}
