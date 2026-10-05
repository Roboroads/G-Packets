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

/** Deletes a stickie from the wall. The client sends it from the stickie's delete button. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RemoveItem implements Packet, JsonSerializable {
    public static final PacketType<RemoveItem> TYPE = PacketType.of("RemoveItem", HMessage.Direction.TOSERVER, Schema.of(RemoveItem.class)
            .integer("furniId"));

    // The stickie's id in the room (WallItem.furniId). The client passes the stickie widget's objectId
    // (RWSUM_STICKIE_SEND_DELETE, deleteWallItem).
    private Integer furniId;

    public static RemoveItem fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RemoveItem fromJson(String json) {
        return Json.parse(RemoveItem.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
