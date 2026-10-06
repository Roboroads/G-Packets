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

/** You chatted too fast: the room's flood filter blocks your chat for a while. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class FloodControl implements Packet, JsonSerializable {
    public static final PacketType<FloodControl> TYPE = PacketType.of("FloodControl", HMessage.Direction.TOCLIENT, Schema.of(FloodControl.class)
            .integer("seconds"));

    // How long you can't chat: the chat input stays blocked until then (RoomChatInputWidget.floodBlocked).
    private Integer seconds;

    public static FloodControl fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static FloodControl fromJson(String json) {
        return Json.parse(FloodControl.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
