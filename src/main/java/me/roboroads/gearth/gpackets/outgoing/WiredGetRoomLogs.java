package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.WiredLogLevel;
import me.roboroads.gearth.gpackets.model.enums.WiredLogSource;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Asks for a page of the wired room logs; the server answers with {@code WiredRoomLogs}. The logs
 * window opens with page 1, both filters on ALL and no query, and asks again every 2.5 seconds
 * while its auto refresh is on.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredGetRoomLogs implements Packet, JsonSerializable {
    public static final PacketType<WiredGetRoomLogs> TYPE = PacketType.of("WiredGetRoomLogs", HMessage.Direction.TOSERVER, Schema.of(WiredGetRoomLogs.class)
            .integer("page")
            .integer("pageSize")
            .enumInt("logLevelFilter", WiredLogLevel.class)
            .enumInt("logSourceFilter", WiredLogSource.class)
            .string("query"));

    // Starts at 1.
    private Integer page;
    // WiredRoomLogsConfig.PAGE_SIZE: the client always sends 50.
    @Builder.Default
    private Integer pageSize = 50;
    // The selected item of the logs window's log_level_menu minus 1, so ALL is -1. Not limited to
    // those: when it asks for another page, the client sends back the filter of the page it has.
    private WiredLogLevel logLevelFilter;
    // The selected item of its log_source_menu minus 1, so ALL is -1. Sent back the same way.
    private WiredLogSource logSourceFilter;
    // The text of the window's filter_input, "" for none. The input takes 400 characters
    // (max_chars), but the client also sends back the query of the page it has, so it isn't limited.
    private String query;

    public static WiredGetRoomLogs fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredGetRoomLogs fromJson(String json) {
        return Json.parse(WiredGetRoomLogs.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
