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

/** You stop spectating the room and enter it as a visitor. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class YouAreNotSpectator implements Packet, JsonSerializable {
    public static final PacketType<YouAreNotSpectator> TYPE = PacketType.of("YouAreNotSpectator", HMessage.Direction.TOCLIENT, Schema.of(YouAreNotSpectator.class)
            .integer("roomId"));

    // The client calls it flatId and ignores the packet unless it's the current room.
    private Integer roomId;

    public static YouAreNotSpectator fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static YouAreNotSpectator fromJson(String json) {
        return Json.parse(YouAreNotSpectator.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
