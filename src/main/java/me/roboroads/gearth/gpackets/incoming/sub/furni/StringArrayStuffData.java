package me.roboroads.gearth.gpackets.incoming.sub.furni;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;

import java.util.List;

/** Format 2: a list of strings. */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class StringArrayStuffData extends StuffData {
    // The first value is the furni's state. For group furni (category 17), the client tells them
    // apart by values 1 to 4 (TradingModel.getGuildFurniType).
    private List<String> values;

    public static StringArrayStuffData fromJson(String json) {
        return Json.parse(StringArrayStuffData.class, json);
    }
}
