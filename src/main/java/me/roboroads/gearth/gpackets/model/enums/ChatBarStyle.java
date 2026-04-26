package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ChatBarStyle {
    DEFAULT(0),
    GENERIC(1),
    ROBOT(2),
    NORMAL_RED(3),
    NORMAL_BLUE(4),
    NORMAL_YELLOW(5),
    NORMAL_GREEN(6),
    NORMAL_GRAY(7),
    FORTUNE_TELLER(8),
    ZOMBIE_HAND(9),
    SKELETON(10),
    NORMAL_SKY_BLUE(11),
    NORMAL_PINK(12),
    NORMAL_PURPLE(13),
    NORMAL_DARK_YELLOW(14),
    NORMAL_DARK_TURQUOISE(15),
    HEARTS(16),
    GOTHICROSE(17),
    STICKINGPLASTER(17),
    PIGLET(19),
    SAUSAGEDOG(20),
    FIRINGMYLAZER(21),
    DRAGON(22),
    STAFF(23),
    BATS(24),
    CONSOLE(25),
    STEAMPUNK_PIPE(26),
    STORM(27),
    PARROT(28),
    PIRATE(29),
    BOT_GUIDE(30),
    BOT_RENTABLE(31),
    SKELETON_STOCK(32),
    BOT_FRANK_LARGE(33),
    NOTIFICATION(34),
    GOAT(35),
    SANTA(36),
    AMBASSADOR(37),
    RADIO(38),
    TEAM_RED(130),
    TEAM_BLUE(131),
    TEAM_YELLOW(132),
    TEAM_GREEN(133),
    NOTIFICATION_RED(200),
    NOTIFICATION_GREEN(201),
    NOTIFICATION_BLUE(202),
    NOTIFICATION_ALERT(210),
    NOTIFICATION_INFO(211),
    NOTIFICATION_WARNING(212),
    NOTIFICATION_WRONG(220),
    NOTIFICATION_WRONG_CIRCLE(221),
    NOTIFICATION_CORRECT(222),
    NOTIFICATION_CORRECT_CIRCLE(223),
    NOTIFICATION_QUESTION_MARK(224),
    NOTIFICATION_QUESTION_MARK_CIRCLE(225),
    NOTIFICATION_ARROW_UP(226),
    NOTIFICATION_ARROW_UP_CIRCLE(227),
    NOTIFICATION_ARROW_DOWN(228),
    NOTIFICATION_ARROW_DOWN_CIRCLE(229),
    NOTIFICATION_SKULL(250),
    NOTIFICATION_SKULL_2(251),
    NOTIFICATION_MAGNIFIER(252);

    @Getter
    @JsonValue
    private final int value;

    public static ChatBarStyle fromValue(int value) {
        for (ChatBarStyle direction : values()) {
            if (direction.value == value) {
                return direction;
            }
        }
        return null;
    }
}
