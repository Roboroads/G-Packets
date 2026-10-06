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
 * Reloads the room or rolls it back: the reload and rollback buttons of the wired menu's settings
 * tab, and the chat input's confirmations of the same.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredUpdateRoom implements Packet, JsonSerializable {
    public static final PacketType<WiredUpdateRoom> TYPE = PacketType.of("WiredUpdateRoom", HMessage.Direction.TOSERVER, Schema.of(WiredUpdateRoom.class)
            .bool("rollBack"));

    // True from the rollback confirmation (onRollbackConfirmed, roll_back_btn,
    // wiredmenu.settings.room_state.roll_back.warning: "Any furni movement or state change since the
    // last room reload will be gone!"), only offered to the room owner and staff. False from reload
    // (onClickReload, reload_room_btn), which needs the write permission. The composer's parameter
    // has no name.
    private Boolean rollBack;

    public static WiredUpdateRoom fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredUpdateRoom fromJson(String json) {
        return Json.parse(WiredUpdateRoom.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
