package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** The server ran into an error while handling one of your packets. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ErrorReport implements Packet, JsonSerializable {
    public static final PacketType<ErrorReport> TYPE = PacketType.of("ErrorReport", HMessage.Direction.TOCLIENT, Schema.of(ErrorReport.class)
            .integer("messageId")
            .integer("errorCode")
            .string("timestamp"));

    // Likely the header id of the packet that failed. The client passes it to the login component's
    // handleErrorMessage (communication/demo/__LS.as), which never uses its second parameter.
    @Unused("The client passes it on but never uses it")
    @Deprecated
    private Integer messageId;
    // The client closes the connection for 1001 to 1019, shows "This room is undergoing a quick
    // maintenance" for 4013 (connection.room.maintenance.desc), and "Sorry, received server error:
    // <code>" (connection.server.error.desc) for anything else.
    private Integer errorCode;
    // The client stores it and nothing reads it.
    @Unused("The client stores it but never reads it")
    @Deprecated
    private String timestamp;

    public static ErrorReport fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ErrorReport fromJson(String json) {
        return Json.parse(ErrorReport.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
