package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.RoomPropertyType;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** The pattern of the room's floor, wallpaper or landscape. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomProperty implements Packet, JsonSerializable {
    public static final PacketType<RoomProperty> TYPE = PacketType.of("RoomProperty", HMessage.Direction.TOCLIENT, Schema.of(RoomProperty.class)
            .enumString("type", RoomPropertyType.class)
            .string("value"));

    private RoomPropertyType type;
    // The pattern id the room engine draws; its defaults are "111" for the floor, "201" for the
    // wallpaper and "1" for the landscape. The client stores it as floorType, wallType,
    // landscapeType or animatedLandscapeType.
    private String value;

    public static RoomProperty fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RoomProperty fromJson(String json) {
        return Json.parse(RoomProperty.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
