package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.chat.ChatLink;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.model.enums.ChatBubbleWidth;
import me.roboroads.gearth.gpackets.model.enums.Gesture;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/**
 * A user in the room whispers to you, or the server echoes your own whisper. The client reads it
 * with the same parser as {@code Chat}; the outgoing {@code Whisper} is what you send.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Whisper implements Packet, JsonSerializable {
    public static final PacketType<Whisper> TYPE = PacketType.of("Whisper", HMessage.Direction.TOCLIENT, Schema.of(Whisper.class)
            .integer("userIndex")
            .string("text")
            .enumInt("gesture", Gesture.class)
            .enumInt("style", ChatBarStyle.class)
            .list("links", ChatLink.SCHEMA)
            .integer("trackingId")
            .optional(a -> a
                    .integer("receiverRoomIndex")
                    .optional(b -> b.enumInt("chatBubbleWidthOverride", ChatBubbleWidth.class))));

    // The speaker's room index (User.userIndex), not their account id: the room engine uses it as
    // the room object id and skips the talk animation when it's your own. The client calls it userId.
    private Integer userIndex;
    // Holds a {0}, {1}, ... placeholder for each link.
    private String text;
    private Gesture gesture;
    // The client calls it styleId.
    private ChatBarStyle style;
    private List<ChatLink> links;
    // RoomChatHandler only reads the tracking id of a Chat; the outgoing Whisper sends none.
    @Unused("The client only reads the tracking id of a Chat")
    @Deprecated
    private Integer trackingId;
    // A room index, likely the recipient's. The client reads it when the server sends it, and
    // nothing uses it.
    @Unused("The client reads it but nothing uses it")
    @Deprecated
    private Integer receiverRoomIndex;
    // Overrides the room's bubble width for this message; -1 (not a named width) keeps the room's.
    private ChatBubbleWidth chatBubbleWidthOverride;

    public static Whisper fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Whisper fromJson(String json) {
        return Json.parse(Whisper.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
