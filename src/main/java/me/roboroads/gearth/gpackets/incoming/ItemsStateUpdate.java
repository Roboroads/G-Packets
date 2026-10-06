package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.furni.WallItemStateUpdate;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/** The state data of several wall furni changes at once. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ItemsStateUpdate implements Packet, JsonSerializable {
    public static final PacketType<ItemsStateUpdate> TYPE = PacketType.of("ItemsStateUpdate", HMessage.Direction.TOCLIENT, Schema.of(ItemsStateUpdate.class)
            .list("items", WallItemStateUpdate.SCHEMA));

    private List<WallItemStateUpdate> items;

    public static ItemsStateUpdate fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ItemsStateUpdate fromJson(String json) {
        return Json.parse(ItemsStateUpdate.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
