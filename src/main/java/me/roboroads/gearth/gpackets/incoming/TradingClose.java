package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.TradingCloseReason;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** The trade closed without completing. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class TradingClose implements Packet, JsonSerializable {
    public static final PacketType<TradingClose> TYPE = PacketType.of("TradingClose", HMessage.Direction.TOCLIENT, Schema.of(TradingClose.class)
            .integer("userId")
            .enumInt("reason", TradingCloseReason.class));

    // The account id of the user who closed it; when it isn't you, the client says the other user
    // cancelled the trade.
    private Integer userId;
    private TradingCloseReason reason;

    public static TradingClose fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static TradingClose fromJson(String json) {
        return Json.parse(TradingClose.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
