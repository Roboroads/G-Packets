package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.room.FurnitureAlias;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/** The furni class names the room engine loads under another name; the answer to {@code GetFurnitureAliases}. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class FurnitureAliases implements Packet, JsonSerializable {
    public static final PacketType<FurnitureAliases> TYPE = PacketType.of("FurnitureAliases", HMessage.Direction.TOCLIENT, Schema.of(FurnitureAliases.class)
            .list("aliases", FurnitureAlias.SCHEMA));

    private List<FurnitureAlias> aliases;

    public static FurnitureAliases fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static FurnitureAliases fromJson(String json) {
        return Json.parse(FurnitureAliases.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
