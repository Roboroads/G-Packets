package me.roboroads.gearth.gpackets.outgoing;

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

/** Walks your avatar to a tile. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class MoveAvatar implements Packet, JsonSerializable {
    public static final PacketType<MoveAvatar> TYPE = PacketType.of("MoveAvatar", HMessage.Direction.TOSERVER, Schema.of(MoveAvatar.class)
            .integer("x")
            .integer("y"));

    private Integer x;
    private Integer y;

    public static MoveAvatar fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static MoveAvatar fromJson(String json) {
        return Json.parse(MoveAvatar.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
