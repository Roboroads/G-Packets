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
 * Places a furni from your inventory in the room. The client also sends it to place a furni you just
 * bought from the catalog. Stickies, bots and pets have packets of their own.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class PlaceObject implements Packet, JsonSerializable {
    public static final PacketType<PlaceObject> TYPE = PacketType.of("PlaceObject", HMessage.Direction.TOSERVER, Schema.of(PlaceObject.class)
            .string("placement"));

    // The item and where it goes, separated by spaces. A floor item: "<itemId> <x> <y> <direction>",
    // with the direction 0 to 7 (degrees divided by 45). A wall item: "<itemId> <location>", with the
    // location in the format of WallItem.location (the composer's wallLocation). The item id is the
    // inventory's FurnitureItem.id. G-Rust calls the string data.
    private String placement;

    public static PlaceObject fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static PlaceObject fromJson(String json) {
        return Json.parse(PlaceObject.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
