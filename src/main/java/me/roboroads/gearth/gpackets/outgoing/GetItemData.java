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
 * Asks for a wall furni's item data. The client sends it when you use a stickie, and opens the stickie
 * when its ItemDataUpdate arrives.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class GetItemData implements Packet, JsonSerializable {
    public static final PacketType<GetItemData> TYPE = PacketType.of("GetItemData", HMessage.Direction.TOSERVER, Schema.of(GetItemData.class)
            .integer("furniId"));

    // The stickie's id in the room (WallItem.furniId). The client's local is objectId (useObject, for
    // ROFCAE_STICKIE).
    private Integer furniId;

    public static GetItemData fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static GetItemData fromJson(String json) {
        return Json.parse(GetItemData.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
