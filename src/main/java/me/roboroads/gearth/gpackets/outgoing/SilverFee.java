package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
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
 * Changes how much silver you put toward the trade's fee: the plus and minus buttons next to the
 * fee. TradeSilverSet holds the amounts.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class SilverFee implements Packet, JsonSerializable {
    public static final PacketType<SilverFee> TYPE = PacketType.of("SilverFee", HMessage.Direction.TOSERVER, Schema.of(SilverFee.class)
            .bool("increase"));

    // True from silver_plus_button, false from silver_minus_button (TradingView; the client's
    // TradingModel.addSilverFee). The packet carries no amount.
    private Boolean increase;

    public static SilverFee fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static SilverFee fromJson(String json) {
        return Json.parse(SilverFee.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
