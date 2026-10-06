package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.TradeOpenFailedReason;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** The trade you asked for with OpenTrading can't start. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class TradeOpenFailed implements Packet, JsonSerializable {
    public static final PacketType<TradeOpenFailed> TYPE = PacketType.of("TradeOpenFailed", HMessage.Direction.TOCLIENT, Schema.of(TradeOpenFailed.class)
            .enumInt("reason", TradeOpenFailedReason.class)
            .string("otherUserName"));

    private TradeOpenFailedReason reason;
    // The user you wanted to trade with; the client puts it in the reason's text.
    private String otherUserName;

    public static TradeOpenFailed fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static TradeOpenFailed fromJson(String json) {
        return Json.parse(TradeOpenFailed.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
