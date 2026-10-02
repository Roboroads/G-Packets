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

/** The hotel closes in a few minutes. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class InfoHotelClosing implements Packet, JsonSerializable {
    public static final PacketType<InfoHotelClosing> TYPE = PacketType.of("InfoHotelClosing", HMessage.Direction.TOCLIENT, Schema.of(InfoHotelClosing.class)
            .integer("minutesUntilClosing"));

    private Integer minutesUntilClosing;

    public static InfoHotelClosing fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static InfoHotelClosing fromJson(String json) {
        return Json.parse(InfoHotelClosing.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
