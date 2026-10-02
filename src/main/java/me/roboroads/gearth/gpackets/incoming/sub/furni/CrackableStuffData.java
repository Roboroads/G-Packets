package me.roboroads.gearth.gpackets.incoming.sub.furni;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;

/** Format 7: a furni you crack open by hitting it. The client's CRACKABLE_TYPE_KEY. */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class CrackableStuffData extends StuffData {
    // The furni's state (getLegacyString).
    private String legacyString;
    // The hits so far (furniture_crackable_hits).
    private Integer hits;
    // The hits it takes to crack it (furniture_crackable_target).
    private Integer target;

    public static CrackableStuffData fromJson(String json) {
        return Json.parse(CrackableStuffData.class, json);
    }
}
