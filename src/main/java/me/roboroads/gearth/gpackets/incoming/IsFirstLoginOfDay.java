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

/** Whether this is your first login today. On the first, the client can open the seasonal calendar. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class IsFirstLoginOfDay implements Packet, JsonSerializable {
    public static final PacketType<IsFirstLoginOfDay> TYPE = PacketType.of("IsFirstLoginOfDay", HMessage.Direction.TOCLIENT, Schema.of(IsFirstLoginOfDay.class)
            .bool("isFirstLoginOfDay"));

    private Boolean isFirstLoginOfDay;

    public static IsFirstLoginOfDay fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static IsFirstLoginOfDay fromJson(String json) {
        return Json.parse(IsFirstLoginOfDay.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
