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
 * Your Builders Club membership: how long it has left and how many furni you may borrow. The
 * catalog header shows you as a full member while {@code secondsLeft} is above 0, in the grace
 * period while {@code secondsLeftWithGrace} is, and on the free trial otherwise
 * (builder.header.status.member, grace and trial).
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class BuildersClubSubscriptionStatus implements Packet, JsonSerializable {
    public static final PacketType<BuildersClubSubscriptionStatus> TYPE = PacketType.of("BuildersClubSubscriptionStatus", HMessage.Direction.TOCLIENT, Schema.of(BuildersClubSubscriptionStatus.class)
            .integer("secondsLeft")
            .integer("furniLimit")
            .integer("maxFurniLimit")
            .optional(s -> s.integer("secondsLeftWithGrace")));

    // Seconds of membership left. The client counts it down itself, and BCFloorPlanEditor only
    // enables its save button while it's above 0 (or for users with security level 4).
    private Integer secondsLeft;
    // How many furni you may borrow; the catalog stops placing at BuildersClubFurniCount.furniCount.
    private Integer furniLimit;
    // The catalog stores it as builderMaxFurniLimit, but nothing reads that. Likely the highest limit
    // a membership can reach; it looks like a server limit, so it isn't marked unused.
    private Integer maxFurniLimit;
    // Seconds left including the grace period after the membership ends. Optional tail: when the
    // server leaves it off, the client uses secondsLeft.
    private Integer secondsLeftWithGrace;

    public static BuildersClubSubscriptionStatus fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static BuildersClubSubscriptionStatus fromJson(String json) {
        return Json.parse(BuildersClubSubscriptionStatus.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
