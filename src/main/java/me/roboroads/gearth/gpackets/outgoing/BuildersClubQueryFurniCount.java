package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
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
 * Asks for the {@code BuildersClubFurniCount}. The client sends it once, when the catalog
 * initializes. It has no parameters, so there's no all-arguments constructor either.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class BuildersClubQueryFurniCount implements Packet, JsonSerializable {
    public static final PacketType<BuildersClubQueryFurniCount> TYPE = PacketType.of("BuildersClubQueryFurniCount", HMessage.Direction.TOSERVER, Schema.of(BuildersClubQueryFurniCount.class));

    public static BuildersClubQueryFurniCount fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static BuildersClubQueryFurniCount fromJson(String json) {
        return Json.parse(BuildersClubQueryFurniCount.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
