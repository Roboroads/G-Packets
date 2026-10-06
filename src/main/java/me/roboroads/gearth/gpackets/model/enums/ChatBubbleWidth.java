package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.roboroads.gearth.gpackets.support.schema.OpenIntEnum;

import java.util.List;

/**
 * How wide the client draws a chat bubble. The client's {@code ChatBubbleWidth.accordingToRoomChatSetting}
 * turns the id into pixels and draws any other id at the normal width. A chat message sends -1 when
 * it doesn't override the room's width; {@link #of} keeps that and any other id this list doesn't
 * name.
 */
public final class ChatBubbleWidth extends OpenIntEnum {
    // 2000 pixels
    public static final ChatBubbleWidth WIDE = new ChatBubbleWidth(0);
    // 350 pixels
    public static final ChatBubbleWidth NORMAL = new ChatBubbleWidth(1);
    // 240 pixels
    public static final ChatBubbleWidth THIN = new ChatBubbleWidth(2);

    private ChatBubbleWidth(int value) {
        super(value);
    }

    /** The named width with this id, or a value that keeps it. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ChatBubbleWidth of(int value) {
        return of(ChatBubbleWidth.class, value, ChatBubbleWidth::new);
    }

    /** The named widths, sorted by id. */
    public static List<ChatBubbleWidth> values() {
        return values(ChatBubbleWidth.class);
    }
}
