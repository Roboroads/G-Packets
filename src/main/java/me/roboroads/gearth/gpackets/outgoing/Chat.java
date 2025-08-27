package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.Users;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;

@Data
@Builder
@Jacksonized
public class Chat implements Packet, JsonSerializable {
    String text;
    Integer chatStyle;
    Integer index;

    public static Chat fromPacket(HPacket packet) {
        String text = packet.readString();
        Integer chatStyle = packet.readInteger();
        Integer index = packet.readInteger();

        return new Chat(text, chatStyle, index);
    }

    public static Users fromJson(String json) {
        return Json.parse(Users.class, json);
    }

    public HPacket toPacket() {
        HPacket packet = new HPacket("Chat", HMessage.Direction.TOSERVER);
        packet.appendString(text);
        packet.appendInt(chatStyle);
        packet.appendInt(index);
        return packet;
    }

}
