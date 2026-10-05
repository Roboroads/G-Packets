package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.furni.FurniOwner;
import me.roboroads.gearth.gpackets.incoming.sub.furni.WallItem;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/** Every wall furni in the room, sent when you enter it. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Items implements Packet, JsonSerializable {
    public static final PacketType<Items> TYPE = PacketType.of("Items", HMessage.Direction.TOCLIENT, Schema.of(Items.class)
            .list("owners", FurniOwner.SCHEMA)
            .list("items", WallItem.SCHEMA));

    // The names of the items' owners. The client gives each item the name of its ownerId.
    private List<FurniOwner> owners;
    private List<WallItem> items;

    public static Items fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Items fromJson(String json) {
        return Json.parse(Items.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
