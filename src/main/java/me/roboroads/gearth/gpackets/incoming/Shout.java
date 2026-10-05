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
 * A user, pet or bot in the room shouts. The client reads it with the same parser as {@code Chat};
 * the outgoing {@code Shout} is what you send.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Shout implements Packet, JsonSerializable {
    public static final PacketType<Shout> TYPE = PacketType.of("Shout", HMessage.Direction.TOCLIENT, Schema.of(Shout.class)
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
    // the room object id. The client calls it userId.
    private Integer userIndex;
    // Holds a {0}, {1}, ... placeholder for each link.
    private String text;
    private Gesture gesture;
    // The client calls it styleId.
    private ChatBarStyle style;
    private List<ChatLink> links;
    // RoomChatHandler only reads the tracking id of a Chat; the outgoing Shout sends none.
    @Unused("The client only reads the tracking id of a Chat")
    @Deprecated
    private Integer trackingId;
    // A room index. The client reads it when the server sends it, and nothing uses it.
    @Unused("The client reads it but nothing uses it")
    @Deprecated
    private Integer receiverRoomIndex;
    // Overrides the room's bubble width for this message; -1 (not a named width) keeps the room's.
    private ChatBubbleWidth chatBubbleWidthOverride;

    public static Shout fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Shout fromJson(String json) {
        return Json.parse(Shout.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
