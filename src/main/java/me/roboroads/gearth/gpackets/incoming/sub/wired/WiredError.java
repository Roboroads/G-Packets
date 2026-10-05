package me.roboroads.gearth.gpackets.incoming.sub.wired;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** One kind of error the room's wired ran into, as the wired menu's monitor tab lists it. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredError implements SubPacket, JsonSerializable {
    public static final Schema<WiredError> SCHEMA = Schema.of(WiredError.class)
            .integer("errorId")
            .string("errorName")
            .string("category")
            .integer("throwCount")
            .longValue("msSinceLastOccurrence");

    // Which error this is. Clicking the name shows the text wiredmenu.error_info.<errorId>; the
    // client's texts explain 0 to 6: 0 the wired usage limit, 1 too many delayed wired events, 2 a
    // server that can't keep up, 3 a room marked as heavy, 4 suspected malicious wired, 5 signals
    // that recurse for too long, 6 too many variables on one user or furni. Kept as an int: the
    // server sends the name itself in errorName.
    private Integer errorId;
    private String errorName;
    private String category;
    private Integer throwCount;
    // For a value below 0, the monitor tab shows "/" instead of a time.
    private Long msSinceLastOccurrence;

    public static WiredError fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
