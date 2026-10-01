package me.roboroads.gearth.gpackets;

import me.roboroads.gearth.gpackets.incoming.RoomSettingsData;
import me.roboroads.gearth.gpackets.incoming.Users;
import me.roboroads.gearth.gpackets.incoming.WiredMovements;
import me.roboroads.gearth.gpackets.incoming.sub.user.Bot;
import me.roboroads.gearth.gpackets.incoming.sub.user.Player;
import me.roboroads.gearth.gpackets.incoming.sub.wired.UserMove;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.Gender;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.model.enums.WiredMovementType;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class JsonRoundTripTest {

    @Test
    public void toJsonWritesFields() {
        Chat chat = Chat.builder().text("hi").style(ChatBarStyle.ROBOT).trackingId(3).build();

        assertEquals("{\"text\":\"hi\",\"style\":2,\"trackingId\":3}", chat.toJson());
    }

    @Test
    public void emptyPacketSerializesToEmptyObject() {
        assertEquals("{}", new EmptyPacket().toJson());
    }

    private static class EmptyPacket implements JsonSerializable {
    }

    @Test
    public void chatRoundTrip() {
        Chat chat = Chat.builder().text("hi").style(ChatBarStyle.GENERIC).build();

        assertEquals(chat, Chat.fromJson(chat.toJson()));
    }

    @Test
    public void chatKeepsAStyleTheLibraryDoesNotName() {
        Chat chat = Chat.fromJson("{\"text\":\"hi\",\"style\":1028,\"trackingId\":3}");

        assertEquals(1028, chat.style().value());
        assertFalse(chat.style().known());
        assertEquals("{\"text\":\"hi\",\"style\":1028,\"trackingId\":3}", chat.toJson());
    }

    @Test
    @SuppressWarnings("deprecation") // sets Player.groupStatus
    public void usersRoundTripKeepsSubtypes() {
        Users users = Users.builder().users(Arrays.asList(
                Player.builder()
                        .id(1).name("Owner").motto("motto").figure("hd-180-1").userIndex(0)
                        .x(1).y(2).z("0.0").bodyDirection(Direction.EAST).type(UserType.PLAYER)
                        .sex(Gender.MALE).groupId(-1).groupStatus(0).groupName("").swimFigure("")
                        .achievementScore(10).isModerator(false).badgesRank(-1)
                        .build(),
                Bot.builder()
                        .id(2).name("Bot").motto("").figure("hd-180-1").userIndex(1)
                        .x(3).y(4).z("0.0").bodyDirection(Direction.NORTH).type(UserType.BOT)
                        .sex(Gender.FEMALE).ownerId(1).ownerName("Owner").skills(Arrays.asList((short) 1, (short) 2))
                        .build()
        )).build();

        Users parsed = Users.fromJson(users.toJson());

        assertEquals(users, parsed);
        assertInstanceOf(Player.class, parsed.users().get(0));
        assertInstanceOf(Bot.class, parsed.users().get(1));
    }

    @Test
    public void wiredMovementsRoundTripKeepsSubtypes() {
        WiredMovements movements = WiredMovements.builder().movements(Collections.singletonList(
                UserMove.builder()
                        .movementType(WiredMovementType.USER_MOVE)
                        .sourceX(1).sourceY(2).targetX(3).targetY(4).sourceZ("0.0").targetZ("1.0")
                        .userIndex(5).isSlide(0).animationTime(500)
                        .bodyDirection(Direction.EAST).headDirection(Direction.EAST)
                        .hasJump(false).jumpPower(0)
                        .build()
        )).build();

        WiredMovements parsed = WiredMovements.fromJson(movements.toJson());

        assertEquals(movements, parsed);
        assertInstanceOf(UserMove.class, parsed.movements().get(0));
    }

    @Test
    public void roomSettingsDataRoundTripKeepsTheModerationSettings() {
        RoomSettingsData data = RoomSettingsWireFormatTest.roomSettingsData();

        assertEquals(data, RoomSettingsData.fromJson(data.toJson()));
    }
}
