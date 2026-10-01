package me.roboroads.gearth.gpackets.outgoing;

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

/** Gives the hand item you carry to another user in the room. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class PassCarryItem implements Packet, JsonSerializable {
    public static final PacketType<PassCarryItem> TYPE = PacketType.of("PassCarryItem", HMessage.Direction.TOSERVER, Schema.of(PassCarryItem.class)
            .integer("userId"));

    // The receiver's account id (User.id), not their room index: InfoStandWidgetHandler sends
    // RoomWidgetUserActionMessage.userId, the key UserDataManager.getUserData looks users up by.
    private Integer userId;

    public static PassCarryItem fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static PassCarryItem fromJson(String json) {
        return Json.parse(PassCarryItem.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
