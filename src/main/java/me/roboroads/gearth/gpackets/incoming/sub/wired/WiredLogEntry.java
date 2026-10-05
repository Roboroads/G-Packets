package me.roboroads.gearth.gpackets.incoming.sub.wired;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.WiredLogLevel;
import me.roboroads.gearth.gpackets.model.enums.WiredLogSource;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** One line of the wired room logs: the client's WiredLogEntry. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredLogEntry implements SubPacket, JsonSerializable {
    public static final Schema<WiredLogEntry> SCHEMA = Schema.of(WiredLogEntry.class)
            .longValue("id")
            .enumByte("logLevel", WiredLogLevel.class)
            .enumByte("logSource", WiredLogSource.class)
            .string("logMessage")
            .longValue("timestamp")
            .string("timestampStr");

    private Long id;
    private WiredLogLevel logLevel;
    private WiredLogSource logSource;
    private String logMessage;
    @Unused("The client shows timestampStr and never reads this")
    @Deprecated
    private Long timestamp;
    // The time as the logs window shows it, formatted by the server.
    private String timestampStr;

    public static WiredLogEntry fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
