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

/** You click a furni in the room. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ClickFurni implements Packet, JsonSerializable {
    public static final PacketType<ClickFurni> TYPE = PacketType.of("ClickFurni", HMessage.Direction.TOSERVER, Schema.of(ClickFurni.class)
            .integer("furniId")
            .integer("unknownInt2"));

    // The furni's id in the room (the clicked room object's objectId). A floor item sends its id, a wall
    // item its id negated (clickRoomObject in the room's object event handler).
    private Integer furniId;
    // No evidence: the client never passes it, so it is always the composer's default 0. The composer
    // keeps it in a member that other client classes name type, so it may be a type.
    @Builder.Default
    private Integer unknownInt2 = 0;

    public static ClickFurni fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ClickFurni fromJson(String json) {
        return Json.parse(ClickFurni.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
