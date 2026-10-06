package me.roboroads.gearth.gpackets.incoming.sub.furni;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;

import java.util.List;

/**
 * Format 6: a high score table. The client never reads a serial for it, so
 * {@code uniqueSerialNumber} and {@code uniqueSeriesSize} stay empty even with the flag.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class HighScoreStuffData extends StuffData {
    // The furni's state (getLegacyString).
    private String legacyString;
    private Integer scoreType;
    private Integer clearType;
    private List<HighScoreData> entries;

    public static HighScoreStuffData fromJson(String json) {
        return Json.parse(HighScoreStuffData.class, json);
    }
}
