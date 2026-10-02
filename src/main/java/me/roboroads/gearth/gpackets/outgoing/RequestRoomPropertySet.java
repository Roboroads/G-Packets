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

/** Applies a wallpaper, floor or landscape from your inventory to the room you're in. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RequestRoomPropertySet implements Packet, JsonSerializable {
    public static final PacketType<RequestRoomPropertySet> TYPE = PacketType.of("RequestRoomPropertySet", HMessage.Direction.TOSERVER, Schema.of(RequestRoomPropertySet.class)
            .integer("itemId"));

    // The inventory item's id (FurniModel: FurnitureItem.id of a wallpaper, floor or landscape, item
    // categories 2, 3 and 4). The catalog sends it for one you just bought and placed. G-Rust calls
    // it room_id.
    private Integer itemId;

    public static RequestRoomPropertySet fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RequestRoomPropertySet fromJson(String json) {
        return Json.parse(RequestRoomPropertySet.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
