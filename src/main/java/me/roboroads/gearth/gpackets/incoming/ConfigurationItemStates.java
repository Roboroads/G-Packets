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
 * Room-wide switches set by configuration furni: whether hand items can be passed, whether the
 * user and furni chooser works, whether furni moves freely and whether furni is invisible.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationItemStates implements Packet, JsonSerializable {
    // The client reads each value after the first only when bytes are left, so the server may stop
    // after any of them.
    public static final PacketType<ConfigurationItemStates> TYPE = PacketType.of("ConfigurationItemStates", HMessage.Direction.TOCLIENT, Schema.of(ConfigurationItemStates.class)
            .bool("isHanditemControlBlocked")
            .optional(a -> a
                    .bool("chooserDisabled")
                    .optional(b -> b
                            .bool("freeFurniMovementsEnabled")
                            .optional(c -> c.bool("invisibleFurni")))));

    // Sets the room variable handitem_control_blocked.
    private Boolean isHanditemControlBlocked;
    // Sets the room variable chooser_disabled.
    private Boolean chooserDisabled;
    // Sets the room variable free_furni_movements_mode.
    private Boolean freeFurniMovementsEnabled;
    // Sets the room variable invisible_furni and hides or shows the floor and wall furni.
    private Boolean invisibleFurni;

    public static ConfigurationItemStates fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ConfigurationItemStates fromJson(String json) {
        return Json.parse(ConfigurationItemStates.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
