package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
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

/**
 * Asks for the {@code RoomEntryTile}. The floor plan editor sends it when it opens and when you
 * reload it. It has no parameters, so there's no all-arguments constructor either.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class GetRoomEntryTile implements Packet, JsonSerializable {
    public static final PacketType<GetRoomEntryTile> TYPE = PacketType.of("GetRoomEntryTile", HMessage.Direction.TOSERVER, Schema.of(GetRoomEntryTile.class));

    public static GetRoomEntryTile fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static GetRoomEntryTile fromJson(String json) {
        return Json.parse(GetRoomEntryTile.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
