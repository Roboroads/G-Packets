package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Turns your avatar towards a tile. The client sends the tile of the avatar you click, unless the
 * room has a wired trigger for clicking users.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class LookTo implements Packet, JsonSerializable {
    public static final PacketType<LookTo> TYPE = PacketType.of("LookTo", HMessage.Direction.TOSERVER, Schema.of(LookTo.class)
            .integer("x")
            .integer("y"));

    private Integer x;
    private Integer y;

    public static LookTo fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static LookTo fromJson(String json) {
        return Json.parse(LookTo.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
