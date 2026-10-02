package me.roboroads.gearth.gpackets.incoming.sub.furni;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;

/** Format 3: a state string and a result. G-Rust and G-Earth call it vote result. */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class VoteResultStuffData extends StuffData {
    // The furni's state (getLegacyString).
    private String legacyString;
    private Integer result;

    public static VoteResultStuffData fromJson(String json) {
        return Json.parse(VoteResultStuffData.class, json);
    }
}
