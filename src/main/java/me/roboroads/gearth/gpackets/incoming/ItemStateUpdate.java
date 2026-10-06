package me.roboroads.gearth.gpackets.incoming;

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

/** A wall furni's state data changes, for example when someone uses it. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ItemStateUpdate implements Packet, JsonSerializable {
    public static final PacketType<ItemStateUpdate> TYPE = PacketType.of("ItemStateUpdate", HMessage.Direction.TOCLIENT, Schema.of(ItemStateUpdate.class)
            .integer("furniId")
            .string("data"));

    // The furni's id in the room (WallItem.furniId), an int here. The client calls it id.
    private Integer furniId;
    // The new WallItem.data. The client calls it itemData and reads the state number from it when it is
    // numeric.
    private String data;

    public static ItemStateUpdate fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ItemStateUpdate fromJson(String json) {
        return Json.parse(ItemStateUpdate.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
