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

/** Whether a {@code WiredSetUserPermanentVariable} worked. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredSetUserPermanentVariableResult implements Packet, JsonSerializable {
    public static final PacketType<WiredSetUserPermanentVariableResult> TYPE = PacketType.of("WiredSetUserPermanentVariableResult", HMessage.Direction.TOCLIENT, Schema.of(WiredSetUserPermanentVariableResult.class)
            .bool("success"));

    // When false, the client shows wiredmenu.variable_management_detail.notification.modification_failed
    // ("Variable modification failed").
    private Boolean success;

    public static WiredSetUserPermanentVariableResult fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredSetUserPermanentVariableResult fromJson(String json) {
        return Json.parse(WiredSetUserPermanentVariableResult.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
