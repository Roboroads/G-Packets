package me.roboroads.gearth.gpackets.outgoing;

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

/** Enters a room of a room network, such as the landing view's room hopper, instead of a room by id. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomNetworkOpenConnection implements Packet, JsonSerializable {
    public static final PacketType<RoomNetworkOpenConnection> TYPE = PacketType.of("RoomNetworkOpenConnection", HMessage.Direction.TOSERVER, Schema.of(RoomNetworkOpenConnection.class)
            .integer("networkId")
            .integer("homeRoomId"));

    // The room hopper sends the landing.view.roomhopper.network.id setting.
    private Integer networkId;
    // Your home room id when the navigator's goToRoomNetwork is asked to go home and you have one,
    // otherwise 0.
    private Integer homeRoomId;

    public static RoomNetworkOpenConnection fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RoomNetworkOpenConnection fromJson(String json) {
        return Json.parse(RoomNetworkOpenConnection.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
