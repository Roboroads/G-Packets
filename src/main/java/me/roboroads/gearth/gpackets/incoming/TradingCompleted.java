package me.roboroads.gearth.gpackets.incoming;

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

/** Both users confirmed and the trade went through. It has no parameters. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class TradingCompleted implements Packet, JsonSerializable {
    public static final PacketType<TradingCompleted> TYPE = PacketType.of("TradingCompleted", HMessage.Direction.TOCLIENT, Schema.of(TradingCompleted.class));

    public static TradingCompleted fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static TradingCompleted fromJson(String json) {
        return Json.parse(TradingCompleted.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
