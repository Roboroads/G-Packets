package me.roboroads.gearth.gpackets.incoming.sub.furni;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;

/** Format 4: no values besides the serial of a limited edition. */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class EmptyStuffData extends StuffData {

    public static EmptyStuffData fromJson(String json) {
        return Json.parse(EmptyStuffData.class, json);
    }
}
