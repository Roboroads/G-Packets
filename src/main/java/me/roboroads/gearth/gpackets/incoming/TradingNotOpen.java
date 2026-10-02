package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** No trade is open. It has no parameters. */
// The client's TradingModel.handleMessageEvent only logs it.
@Unused("The client only logs it")
@Deprecated
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class TradingNotOpen implements Packet, JsonSerializable {
    public static final PacketType<TradingNotOpen> TYPE = PacketType.of("TradingNotOpen", HMessage.Direction.TOCLIENT, Schema.of(TradingNotOpen.class));

    public static TradingNotOpen fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static TradingNotOpen fromJson(String json) {
        return Json.parse(TradingNotOpen.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
