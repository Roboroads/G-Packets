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

/** The hotel has closed, and opens again at the given time. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class InfoHotelClosed implements Packet, JsonSerializable {
    public static final PacketType<InfoHotelClosed> TYPE = PacketType.of("InfoHotelClosed", HMessage.Direction.TOCLIENT, Schema.of(InfoHotelClosed.class)
            .integer("openHour")
            .integer("openMinute")
            .bool("userThrownOutAtClose"));

    private Integer openHour;
    private Integer openMinute;
    // Picks the alert: "you have been disconnected from the Hotel" (opening.hours.disconnected)
    // when true, "The Hotel has been closed" (opening.hours.closed) when false.
    private Boolean userThrownOutAtClose;

    public static InfoHotelClosed fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static InfoHotelClosed fromJson(String json) {
        return Json.parse(InfoHotelClosed.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
