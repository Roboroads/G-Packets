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

/** You can't log in because the hotel is closed; it opens again at the given time. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class LoginFailedHotelClosed implements Packet, JsonSerializable {
    public static final PacketType<LoginFailedHotelClosed> TYPE = PacketType.of("LoginFailedHotelClosed", HMessage.Direction.TOCLIENT, Schema.of(LoginFailedHotelClosed.class)
            .integer("openHour")
            .integer("openMinute"));

    private Integer openHour;
    private Integer openMinute;

    public static LoginFailedHotelClosed fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static LoginFailedHotelClosed fromJson(String json) {
        return Json.parse(LoginFailedHotelClosed.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
