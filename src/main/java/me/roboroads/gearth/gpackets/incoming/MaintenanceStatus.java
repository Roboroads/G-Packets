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

/** The hotel goes down for maintenance soon. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceStatus implements Packet, JsonSerializable {
    public static final PacketType<MaintenanceStatus> TYPE = PacketType.of("MaintenanceStatus", HMessage.Direction.TOCLIENT, Schema.of(MaintenanceStatus.class)
            .bool("isInMaintenance")
            .integer("minutesUntilMaintenance")
            .optional(s -> s.integer("durationMinutes")));

    // The client stores it and nothing reads it.
    private Boolean isInMaintenance;
    private Integer minutesUntilMaintenance;
    // How long the maintenance takes: "we'll be right back in approximately %d% minutes"
    // (maintenance.shutdown). The client calls it duration. Only read when bytes are left; the
    // client uses 15 when it's missing.
    private Integer durationMinutes;

    public static MaintenanceStatus fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static MaintenanceStatus fromJson(String json) {
        return Json.parse(MaintenanceStatus.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
