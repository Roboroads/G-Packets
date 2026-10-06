package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.WiredMenuErrorCode;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** The server refused a wired menu request. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredMenuError implements Packet, JsonSerializable {
    public static final PacketType<WiredMenuError> TYPE = PacketType.of("WiredMenuError", HMessage.Direction.TOCLIENT, Schema.of(WiredMenuError.class)
            .enumShort("errorCode", WiredMenuErrorCode.class));

    // A short on the wire.
    private WiredMenuErrorCode errorCode;

    public static WiredMenuError fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredMenuError fromJson(String json) {
        return Json.parse(WiredMenuError.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
