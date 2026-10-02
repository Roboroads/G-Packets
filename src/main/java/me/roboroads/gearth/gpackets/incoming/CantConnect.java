package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.CantConnectReason;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** You can't enter the room: it's full, you're banned or blocked, or the queue failed. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class CantConnect implements Packet, JsonSerializable {
    public static final PacketType<CantConnect> TYPE = PacketType.of("CantConnect", HMessage.Direction.TOCLIENT, Schema.of(CantConnect.class)
            .enumInt("reason", CantConnectReason.class)
            .when("reason", CantConnectReason.QUEUE_ERROR, s -> s.string("parameter")));

    private CantConnectReason reason;
    // Only sent for QUEUE_ERROR. The client shows the text room.queue.error.<parameter>; the texts
    // have c, e1, generic, na, queue_full, room_session and spectator_mode_full.
    private String parameter;

    public static CantConnect fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static CantConnect fromJson(String json) {
        return Json.parse(CantConnect.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
