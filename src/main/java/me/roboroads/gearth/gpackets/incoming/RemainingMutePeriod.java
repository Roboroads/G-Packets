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

/**
 * You're muted. The client shows it as a chat bubble from yourself: widget.chatbubble.mutetime,
 * "You are muted! The mute will expire in %hours% hours, %minutes% minutes and %seconds% seconds."
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RemainingMutePeriod implements Packet, JsonSerializable {
    public static final PacketType<RemainingMutePeriod> TYPE = PacketType.of("RemainingMutePeriod", HMessage.Direction.TOCLIENT, Schema.of(RemainingMutePeriod.class)
            .integer("secondsRemaining"));

    private Integer secondsRemaining;

    public static RemainingMutePeriod fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RemainingMutePeriod fromJson(String json) {
        return Json.parse(RemainingMutePeriod.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
