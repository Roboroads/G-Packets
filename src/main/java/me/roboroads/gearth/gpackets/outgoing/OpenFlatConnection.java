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

/** Enters a room. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class OpenFlatConnection implements Packet, JsonSerializable {
    public static final PacketType<OpenFlatConnection> TYPE = PacketType.of("OpenFlatConnection", HMessage.Direction.TOSERVER, Schema.of(OpenFlatConnection.class)
            .integer("roomId")
            .string("password")
            .integer("unknownInt3"));

    // RoomSession.roomId.
    private Integer roomId;
    // RoomSession.roomPassword; empty for a room without a password.
    private String password;
    // No evidence: the composer's third parameter defaults to -1 and no caller passes it, so the
    // client always sends -1. G-Rust calls it _unknown.
    @Builder.Default
    private Integer unknownInt3 = -1;

    public static OpenFlatConnection fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static OpenFlatConnection fromJson(String json) {
        return Json.parse(OpenFlatConnection.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
