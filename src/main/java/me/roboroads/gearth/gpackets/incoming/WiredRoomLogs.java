package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.wired.WiredLogEntry;
import me.roboroads.gearth.gpackets.model.enums.WiredLogLevel;
import me.roboroads.gearth.gpackets.model.enums.WiredLogSource;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/**
 * One page of the wired room logs, the answer to {@code WiredGetRoomLogs}: the client's
 * WiredLogPage. It ends with the filters the page was made with, each after a flag that says whether
 * it is set.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredRoomLogs implements Packet, JsonSerializable {
    public static final PacketType<WiredRoomLogs> TYPE = PacketType.of("WiredRoomLogs", HMessage.Direction.TOCLIENT, Schema.of(WiredRoomLogs.class)
            .integer("totalEntries")
            .integer("currentPage")
            .integer("pageSize")
            .list("entries", WiredLogEntry.SCHEMA)
            .bool("hasLogLevelFilter")
            .when("hasLogLevelFilter", true, s -> s.enumByte("logLevelFilter", WiredLogLevel.class))
            .bool("hasLogSourceFilter")
            .when("hasLogSourceFilter", true, s -> s.enumByte("logSourceFilter", WiredLogSource.class))
            .bool("hasQuery")
            .when("hasQuery", true, s -> s.string("query")));

    private Integer totalEntries;
    // Starts at 1.
    private Integer currentPage;
    // Client: amount. The logs window drops a page whose size isn't its own page size, 50.
    private Integer pageSize;
    // Client: elements.
    private List<WiredLogEntry> entries;
    // Without a filter, the client takes -1: WiredLogLevel.ALL, the same for the source.
    private Boolean hasLogLevelFilter;
    private WiredLogLevel logLevelFilter;
    private Boolean hasLogSourceFilter;
    private WiredLogSource logSourceFilter;
    // Without a query, the client sends "" when it asks for the page again.
    private Boolean hasQuery;
    // The filter text; the logs window puts it back in its filter input.
    private String query;

    public static WiredRoomLogs fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredRoomLogs fromJson(String json) {
        return Json.parse(WiredRoomLogs.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
