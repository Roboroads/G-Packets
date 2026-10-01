package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.roboroads.gearth.gpackets.support.schema.OpenIntEnum;

import java.util.List;

/**
 * A chat bubble style. The ids and names come from the client's style list,
 * binaryData/455_chatstyles_xml; a new name is the style's assetId in upper case. New styles ship
 * as data, so {@link #of} keeps an id this list doesn't have.
 */
public final class ChatBarStyle extends OpenIntEnum {
    public static final ChatBarStyle DEFAULT = new ChatBarStyle(0);
    public static final ChatBarStyle GENERIC = new ChatBarStyle(1);
    public static final ChatBarStyle ROBOT = new ChatBarStyle(2);
    public static final ChatBarStyle NORMAL_RED = new ChatBarStyle(3);
    public static final ChatBarStyle NORMAL_BLUE = new ChatBarStyle(4);
    public static final ChatBarStyle NORMAL_YELLOW = new ChatBarStyle(5);
    public static final ChatBarStyle NORMAL_GREEN = new ChatBarStyle(6);
    public static final ChatBarStyle NORMAL_GRAY = new ChatBarStyle(7);
    public static final ChatBarStyle FORTUNE_TELLER = new ChatBarStyle(8);
    public static final ChatBarStyle ZOMBIE_HAND = new ChatBarStyle(9);
    public static final ChatBarStyle SKELETON = new ChatBarStyle(10);
    public static final ChatBarStyle NORMAL_SKY_BLUE = new ChatBarStyle(11);
    public static final ChatBarStyle NORMAL_PINK = new ChatBarStyle(12);
    public static final ChatBarStyle NORMAL_PURPLE = new ChatBarStyle(13);
    public static final ChatBarStyle NORMAL_DARK_YELLOW = new ChatBarStyle(14);
    public static final ChatBarStyle NORMAL_DARK_TURQUOISE = new ChatBarStyle(15);
    public static final ChatBarStyle HEARTS = new ChatBarStyle(16);
    public static final ChatBarStyle GOTHICROSE = new ChatBarStyle(17);
    public static final ChatBarStyle PIGLET = new ChatBarStyle(19);
    public static final ChatBarStyle SAUSAGEDOG = new ChatBarStyle(20);
    public static final ChatBarStyle FIRINGMYLAZER = new ChatBarStyle(21);
    public static final ChatBarStyle DRAGON = new ChatBarStyle(22);
    public static final ChatBarStyle STAFF = new ChatBarStyle(23);
    public static final ChatBarStyle BATS = new ChatBarStyle(24);
    public static final ChatBarStyle CONSOLE = new ChatBarStyle(25);
    public static final ChatBarStyle STEAMPUNK_PIPE = new ChatBarStyle(26);
    public static final ChatBarStyle STORM = new ChatBarStyle(27);
    public static final ChatBarStyle PARROT = new ChatBarStyle(28);
    public static final ChatBarStyle PIRATE = new ChatBarStyle(29);
    public static final ChatBarStyle BOT_GUIDE = new ChatBarStyle(30);
    public static final ChatBarStyle BOT_RENTABLE = new ChatBarStyle(31);
    public static final ChatBarStyle SKELETON_STOCK = new ChatBarStyle(32);
    public static final ChatBarStyle BOT_FRANK_LARGE = new ChatBarStyle(33);
    public static final ChatBarStyle NOTIFICATION = new ChatBarStyle(34);
    public static final ChatBarStyle GOAT = new ChatBarStyle(35);
    public static final ChatBarStyle SANTA = new ChatBarStyle(36);
    public static final ChatBarStyle AMBASSADOR = new ChatBarStyle(37);
    public static final ChatBarStyle RADIO = new ChatBarStyle(38);
    public static final ChatBarStyle SNOWSTORM_RED = new ChatBarStyle(120);
    public static final ChatBarStyle SNOWSTORM_BLUE = new ChatBarStyle(121);
    public static final ChatBarStyle TEAM_RED = new ChatBarStyle(130);
    public static final ChatBarStyle TEAM_BLUE = new ChatBarStyle(131);
    public static final ChatBarStyle TEAM_YELLOW = new ChatBarStyle(132);
    public static final ChatBarStyle TEAM_GREEN = new ChatBarStyle(133);
    public static final ChatBarStyle NOTIFICATION_RED = new ChatBarStyle(200);
    public static final ChatBarStyle NOTIFICATION_GREEN = new ChatBarStyle(201);
    public static final ChatBarStyle NOTIFICATION_BLUE = new ChatBarStyle(202);
    public static final ChatBarStyle NOTIFICATION_ALERT = new ChatBarStyle(210);
    public static final ChatBarStyle NOTIFICATION_INFO = new ChatBarStyle(211);
    public static final ChatBarStyle NOTIFICATION_WARNING = new ChatBarStyle(212);
    public static final ChatBarStyle NOTIFICATION_WRONG = new ChatBarStyle(220);
    public static final ChatBarStyle NOTIFICATION_WRONG_CIRCLE = new ChatBarStyle(221);
    public static final ChatBarStyle NOTIFICATION_CORRECT = new ChatBarStyle(222);
    public static final ChatBarStyle NOTIFICATION_CORRECT_CIRCLE = new ChatBarStyle(223);
    public static final ChatBarStyle NOTIFICATION_QUESTION_MARK = new ChatBarStyle(224);
    public static final ChatBarStyle NOTIFICATION_QUESTION_MARK_CIRCLE = new ChatBarStyle(225);
    public static final ChatBarStyle NOTIFICATION_ARROW_UP = new ChatBarStyle(226);
    public static final ChatBarStyle NOTIFICATION_ARROW_UP_CIRCLE = new ChatBarStyle(227);
    public static final ChatBarStyle NOTIFICATION_ARROW_DOWN = new ChatBarStyle(228);
    public static final ChatBarStyle NOTIFICATION_ARROW_DOWN_CIRCLE = new ChatBarStyle(229);
    public static final ChatBarStyle NOTIFICATION_SKULL = new ChatBarStyle(250);
    public static final ChatBarStyle NOTIFICATION_SKULL_2 = new ChatBarStyle(251);
    public static final ChatBarStyle NOTIFICATION_MAGNIFIER = new ChatBarStyle(252);

    // NFT styles: ids 1000 to 9999 (RoomChatInputView.isNftChatStyle). The client only offers the
    // ones the user owns.
    public static final ChatBarStyle NFT_HABBO_AVATAR_BRONZE = new ChatBarStyle(1000);
    public static final ChatBarStyle NFT_HABBO_AVATAR_GOLD = new ChatBarStyle(1001);
    public static final ChatBarStyle NFT_HABBO_AVATAR_DIAMOND = new ChatBarStyle(1002);
    public static final ChatBarStyle NFT_HABBO_AVATAR_RAINBOW = new ChatBarStyle(1003);
    public static final ChatBarStyle NFT_HABBO_AVATAR_TRIPPY = new ChatBarStyle(1004);
    public static final ChatBarStyle NFT_HABBO_AVATAR_ULTRA_TRIPPY = new ChatBarStyle(1005);
    public static final ChatBarStyle NFT_MVHQ = new ChatBarStyle(1006);
    public static final ChatBarStyle NFT_METAKEY = new ChatBarStyle(1007);
    public static final ChatBarStyle NFT_CRAFTED_HABBO_AVATAR = new ChatBarStyle(1010);
    public static final ChatBarStyle NFT_BALLOON_ORANGE = new ChatBarStyle(1011);
    public static final ChatBarStyle NFT_BALLOON_BLUE = new ChatBarStyle(1012);
    public static final ChatBarStyle NFT_ORIGAMI_ORANGE = new ChatBarStyle(1013);
    public static final ChatBarStyle NFT_ORIGAMI_BLUE = new ChatBarStyle(1014);
    public static final ChatBarStyle NFT_CHOCOLATE_DARK = new ChatBarStyle(1015);
    public static final ChatBarStyle NFT_CHOCOLATE_WHITE = new ChatBarStyle(1016);
    public static final ChatBarStyle NFT_CLAY = new ChatBarStyle(1017);
    public static final ChatBarStyle NFT_SCROLL = new ChatBarStyle(1018);
    public static final ChatBarStyle NFT_PILLOW = new ChatBarStyle(1019);
    public static final ChatBarStyle NFT_BOBBA = new ChatBarStyle(1020);
    public static final ChatBarStyle NFT_PINKTUBE = new ChatBarStyle(1021);
    public static final ChatBarStyle NFT_KEYCAPS = new ChatBarStyle(1022);
    public static final ChatBarStyle NFT_XMAS22 = new ChatBarStyle(1023);
    public static final ChatBarStyle NFT_ROCKY = new ChatBarStyle(1024);
    public static final ChatBarStyle NFT_ICE = new ChatBarStyle(1025);
    public static final ChatBarStyle NFT_AURORA = new ChatBarStyle(1026);
    public static final ChatBarStyle NFT_MONEY = new ChatBarStyle(1027);

    // Purchasable styles: ids 10000 to 99999 (RoomChatInputView.isPurchasableStyle). The client only
    // offers the ones the user owns.
    public static final ChatBarStyle RECYCLED = new ChatBarStyle(10000);

    private ChatBarStyle(int value) {
        super(value);
    }

    /** The named style with this id, or a value that keeps it. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ChatBarStyle of(int value) {
        return of(ChatBarStyle.class, value, ChatBarStyle::new);
    }

    /** The named styles, sorted by id. */
    public static List<ChatBarStyle> values() {
        return values(ChatBarStyle.class);
    }
}
