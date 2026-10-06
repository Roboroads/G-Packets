package me.roboroads.gearth.gpackets.outgoing;

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

/**
 * Cancels the trade: the cancel button before both users accepted, or leaving the trade view. The
 * client doesn't send it once you confirmed a trade with NFT assets or a silver fee. It has no
 * parameters.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class CloseTrading implements Packet, JsonSerializable {
    public static final PacketType<CloseTrading> TYPE = PacketType.of("CloseTrading", HMessage.Direction.TOSERVER, Schema.of(CloseTrading.class));

    public static CloseTrading fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static CloseTrading fromJson(String json) {
        return Json.parse(CloseTrading.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
