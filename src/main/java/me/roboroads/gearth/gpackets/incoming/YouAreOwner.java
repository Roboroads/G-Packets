package me.roboroads.gearth.gpackets.incoming;

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

/** You own the room you're in. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class YouAreOwner implements Packet, JsonSerializable {
    public static final PacketType<YouAreOwner> TYPE = PacketType.of("YouAreOwner", HMessage.Direction.TOCLIENT, Schema.of(YouAreOwner.class)
            .integer("roomId"));

    // The client calls it flatId but never reads it: RoomPermissionsHandler marks the session of the
    // room you're in as yours, and BuilderCatalogWidget only refreshes its buttons.
    private Integer roomId;

    public static YouAreOwner fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static YouAreOwner fromJson(String json) {
        return Json.parse(YouAreOwner.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
