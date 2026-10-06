package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * Your rights in a room, from none to staff. Named after the client's
 * {@code RoomControllerLevelEnum}. The room session only accepts 0 to 5 and treats any other level
 * as {@link #NOT_CONTROLLER}.
 */
@RequiredArgsConstructor
public enum RoomControllerLevel implements IntEnum {
    NOT_CONTROLLER(0),
    // Rights the owner gave you (AssignRights). Most of the client's checks are "level 1 or up".
    ROOM_CONTROLLER(1),
    // The client calls it GUILD_MEMBER.
    GROUP_MEMBER(2),
    // The client calls it GUILD_ADMIN. In a group room, level 3 and up may moderate when the room's
    // moderation settings allow group admins.
    GROUP_ADMIN(3),
    ROOM_OWNER(4),
    // Staff.
    MODERATOR(5);

    @Getter
    @JsonValue
    private final int value;

    public static RoomControllerLevel fromValue(int value) {
        for (RoomControllerLevel level : values()) {
            if (level.value == value) {
                return level;
            }
        }
        return null;
    }
}
