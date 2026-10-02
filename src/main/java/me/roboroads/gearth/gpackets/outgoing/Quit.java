package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Leaves the room you're in, or stops waiting at its doorbell or in its queue. It has no
 * parameters, so there's no all-arguments constructor either.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class Quit implements Packet, JsonSerializable {
    public static final PacketType<Quit> TYPE = PacketType.of("Quit", HMessage.Direction.TOSERVER, Schema.of(Quit.class));

    public static Quit fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Quit fromJson(String json) {
        return Json.parse(Quit.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
