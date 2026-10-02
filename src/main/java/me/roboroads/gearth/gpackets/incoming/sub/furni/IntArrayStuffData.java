package me.roboroads.gearth.gpackets.incoming.sub.furni;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;

import java.util.List;

/** Format 5: a list of ints. The client's __212 (INT_ARRAY_TYPE_KEY). */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class IntArrayStuffData extends StuffData {
    // The first value is the furni's state.
    private List<Integer> values;

    public static IntArrayStuffData fromJson(String json) {
        return Json.parse(IntArrayStuffData.class, json);
    }
}
