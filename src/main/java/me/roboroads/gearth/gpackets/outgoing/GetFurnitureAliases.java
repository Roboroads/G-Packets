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
 * Asks for the {@code FurnitureAliases}. The client sends it once, right after login. It has no
 * parameters, so there's no all-arguments constructor either.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class GetFurnitureAliases implements Packet, JsonSerializable {
    public static final PacketType<GetFurnitureAliases> TYPE = PacketType.of("GetFurnitureAliases", HMessage.Direction.TOSERVER, Schema.of(GetFurnitureAliases.class));

    public static GetFurnitureAliases fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static GetFurnitureAliases fromJson(String json) {
        return Json.parse(GetFurnitureAliases.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
