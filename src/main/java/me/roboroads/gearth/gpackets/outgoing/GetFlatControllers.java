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

/**
 * Asks for the users with rights in a room; the server answers with {@code FlatControllers}. The
 * room settings send it when their rights tab opens.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class GetFlatControllers implements Packet, JsonSerializable {
    public static final PacketType<GetFlatControllers> TYPE = PacketType.of("GetFlatControllers", HMessage.Direction.TOSERVER, Schema.of(GetFlatControllers.class)
            .integer("roomId"));

    private Integer roomId;

    public static GetFlatControllers fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static GetFlatControllers fromJson(String json) {
        return Json.parse(GetFlatControllers.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
