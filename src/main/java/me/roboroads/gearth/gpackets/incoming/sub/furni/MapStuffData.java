package me.roboroads.gearth.gpackets.incoming.sub.furni;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;

import java.util.List;

/** Format 1: string keys with string values. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class MapStuffData extends StuffData {
    // In wire order. The keys depend on the furni: the client looks up state, rarity,
    // contents_count, chest_name, renterId, internalLink and videoId, among others.
    private List<MapStuffDataEntry> entries;

    public static MapStuffData fromJson(String json) {
        return Json.parse(MapStuffData.class, json);
    }
}
