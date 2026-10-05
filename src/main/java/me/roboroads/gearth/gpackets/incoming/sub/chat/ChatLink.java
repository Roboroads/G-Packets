package me.roboroads.gearth.gpackets.incoming.sub.chat;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * A link in a chat message. The message text holds a {@code {0}}, {@code {1}}, ... placeholder for
 * each link. The client's parser reads each link as a plain array of its three values.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ChatLink implements SubPacket, JsonSerializable {
    public static final Schema<ChatLink> SCHEMA = Schema.of(ChatLink.class)
            .string("unknownString1")
            .string("url")
            .bool("unknownBoolean3");

    // No getter or named local. Maybe the link as the user typed it, unverified.
    @Unused("The chat bubbles only read the link's second value")
    @Deprecated
    private String unknownString1;
    // ChatBubble and PooledChatBubble put it in an <a href> (as the href and the link text) in place
    // of the placeholder. They only do that for the first link.
    private String url;
    // No getter or named local. Maybe whether the link is trusted, unverified.
    @Unused("The chat bubbles only read the link's second value")
    @Deprecated
    private Boolean unknownBoolean3;

    public static ChatLink fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
