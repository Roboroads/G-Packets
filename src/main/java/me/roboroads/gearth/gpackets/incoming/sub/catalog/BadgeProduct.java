package me.roboroads.gearth.gpackets.incoming.sub.catalog;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class BadgeProduct extends Product {

    public static BadgeProduct fromJson(String json) {
        return Json.parse(BadgeProduct.class, json);
    }
}
