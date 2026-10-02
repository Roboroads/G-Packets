package me.roboroads.gearth.gpackets.incoming.sub.furni;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;

/** Format 0: one string, usually the furni's state ("0", "1", ...). */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class LegacyStuffData extends StuffData {
    // What the client's getLegacyString returns; it reads a number in it as the furni's state.
    private String legacyString;

    public static LegacyStuffData fromJson(String json) {
        return Json.parse(LegacyStuffData.class, json);
    }
}
