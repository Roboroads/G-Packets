package me.roboroads.gearth.gpackets.incoming.sub.catalog;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.Utils;

import java.util.List;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Localization implements SubPacket, JsonSerializable {
    private List<String> images;
    private List<String> texts;

    public static Localization fromPacket(HPacket packet) {
        return Localization.builder()
                .images(Utils.readList(packet, HPacket::readString))
                .texts(Utils.readList(packet, HPacket::readString))
                .build();
    }

    @Override
    public void appendPacket(HPacket packet) {
        packet.appendInt(images != null ? images.size() : 0);
        if (images != null) {
            for (String image : images) {
                packet.appendString(image != null ? image : "");
            }
        }
        packet.appendInt(texts != null ? texts.size() : 0);
        if (texts != null) {
            for (String text : texts) {
                packet.appendString(text != null ? text : "");
            }
        }
    }
}