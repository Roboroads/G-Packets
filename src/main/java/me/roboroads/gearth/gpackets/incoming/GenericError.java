package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.GenericErrorCode;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** An error code from the server, for a failed login or a room you can't enter or change. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class GenericError implements Packet, JsonSerializable {
    public static final PacketType<GenericError> TYPE = PacketType.of("GenericError", HMessage.Direction.TOCLIENT, Schema.of(GenericError.class)
            .enumInt("errorCode", GenericErrorCode.class));

    private GenericErrorCode errorCode;

    public static GenericError fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static GenericError fromJson(String json) {
        return Json.parse(GenericError.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
