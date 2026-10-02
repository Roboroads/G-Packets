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
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** You're connected to a room. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class OpenConnection implements Packet, JsonSerializable {
    public static final PacketType<OpenConnection> TYPE = PacketType.of("OpenConnection", HMessage.Direction.TOCLIENT, Schema.of(OpenConnection.class)
            .integer("roomId"));

    // The client calls it flatId; it marks that room's session as connected.
    private Integer roomId;

    public static OpenConnection fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static OpenConnection fromJson(String json) {
        return Json.parse(OpenConnection.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
