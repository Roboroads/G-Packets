package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.ChatFloodSensitivity;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** The chat settings of the room you're in. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomChatSettings implements Packet, JsonSerializable {
    public static final PacketType<RoomChatSettings> TYPE = PacketType.of("RoomChatSettings", HMessage.Direction.TOCLIENT, Schema.of(RoomChatSettings.class)
            .enumInt("chatFloodSensitivity", ChatFloodSensitivity.class));

    // The client builds its chat settings from this alone (fromFloodSensitivity), like
    // RoomSettingsData.chatFloodSensitivity. The free flow chat stores it, but nothing reads the
    // flood sensitivity back from its settings.
    private ChatFloodSensitivity chatFloodSensitivity;

    public static RoomChatSettings fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RoomChatSettings fromJson(String json) {
        return Json.parse(RoomChatSettings.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
