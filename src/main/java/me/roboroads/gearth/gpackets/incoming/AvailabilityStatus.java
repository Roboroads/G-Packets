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

/** Whether the hotel is open, and whether it's about to shut down. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityStatus implements Packet, JsonSerializable {
    public static final PacketType<AvailabilityStatus> TYPE = PacketType.of("AvailabilityStatus", HMessage.Direction.TOCLIENT, Schema.of(AvailabilityStatus.class)
            .bool("isOpen")
            .bool("isShuttingDown")
            .optional(s -> s.bool("isAuthentic")));

    // The client stores it as SessionDataManager.systemOpen, which nothing reads.
    private Boolean isOpen;
    // The client calls it onShutDown and stores it as systemShutDown. While it's true, the infostand
    // turns trading off.
    private Boolean isShuttingDown;
    // Only read when bytes are left. The client's getter is "isAuthentic" followed by the game's
    // name, and nothing calls it.
    private Boolean isAuthentic;

    public static AvailabilityStatus fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static AvailabilityStatus fromJson(String json) {
        return Json.parse(AvailabilityStatus.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
