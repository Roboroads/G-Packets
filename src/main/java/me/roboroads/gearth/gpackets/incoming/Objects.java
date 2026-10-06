package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.furni.FloorItem;
import me.roboroads.gearth.gpackets.incoming.sub.furni.FurniOwner;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/** Every floor furni in the room, sent when you enter it. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Objects implements Packet, JsonSerializable {
    public static final PacketType<Objects> TYPE = PacketType.of("Objects", HMessage.Direction.TOCLIENT, Schema.of(Objects.class)
            .list("owners", FurniOwner.SCHEMA)
            .list("objects", FloorItem.SCHEMA));

    // The names of the objects' owners. The client gives each object the name of its ownerId.
    private List<FurniOwner> owners;
    private List<FloorItem> objects;

    public static Objects fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Objects fromJson(String json) {
        return Json.parse(Objects.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
