package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.ChatFloodSensitivity;
import me.roboroads.gearth.gpackets.model.enums.DoorMode;
import me.roboroads.gearth.gpackets.model.enums.MaximumVisitors;
import me.roboroads.gearth.gpackets.model.enums.RoomModerationPermission;
import me.roboroads.gearth.gpackets.model.enums.RoomThickness;
import me.roboroads.gearth.gpackets.model.enums.TradeMode;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.each;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.maxLength;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.maxSize;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.not;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.range;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.requiresVip;

/**
 * Saves a room's settings; the server answers with {@code RoomSettingsSaved} or
 * {@code RoomSettingsSaveError}. The order differs from {@code RoomSettingsData}: the maximum
 * visitors come before the category, the moderation settings sit before the flood sensitivity,
 * and the four room flags are booleans here.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class SaveRoomSettings implements Packet, JsonSerializable {
    // Limits from the client's room settings (RoomSettingsCtrl): see the field comments.
    public static final PacketType<SaveRoomSettings> TYPE = PacketType.of("SaveRoomSettings", HMessage.Direction.TOSERVER, Schema.of(SaveRoomSettings.class)
            .integer("roomId")
            .string("name", maxLength(60))
            .string("description", maxLength(255))
            .enumInt("doorMode", DoorMode.class)
            .string("password", maxLength(30))
            .enumInt("maximumVisitors", MaximumVisitors.class)
            .integer("categoryId")
            .list("tags", WireType.STRING, maxSize(2), each(maxLength(30)))
            .enumInt("tradeMode", TradeMode.class)
            .bool("allowPets")
            .bool("allowFoodConsume")
            .bool("allowWalkThrough")
            .bool("hideWalls", requiresVip())
            .enumInt("wallThickness", RoomThickness.class, requiresVip())
            .enumInt("floorThickness", RoomThickness.class, requiresVip())
            .enumInt("whoCanMute", RoomModerationPermission.class, not(RoomModerationPermission.ALL))
            .enumInt("whoCanKick", RoomModerationPermission.class)
            .enumInt("whoCanBan", RoomModerationPermission.class, not(RoomModerationPermission.ALL))
            .enumInt("chatFloodSensitivity", ChatFloodSensitivity.class)
            .bool("leaveOnDoorTileEnabled", requiresVip())
            .bool("idleSleepEnabled", requiresVip())
            .integer("idleSleepTimeoutSeconds", range(30, 3600).orZero(), requiresVip())
            .bool("idleAutokickEnabled", requiresVip())
            .integer("idleAutokickTimeoutSeconds", range(60, 36000).orZero(), requiresVip())
            .bool("muteAllPets"));

    private Integer roomId;
    // At most 60 characters: the room name field caps typing at 60 (RoomSettingsCtrl:513). The client
    // resends a stored name as it loaded it (RoomSettingsCtrl:718), and the server keeps names within 60.
    private String name;
    // At most 255 characters, like the name: the field caps typing (RoomSettingsCtrl:514), the client
    // resends a stored description as loaded (RoomSettingsCtrl:719), and the server keeps it within 255.
    private String description;
    private DoorMode doorMode;
    // The client sends "" unless the door mode is password. At most 30 characters (RoomSettingsCtrl:517-518).
    private String password;
    private MaximumVisitors maximumVisitors;
    private Integer categoryId;
    // Two tag inputs of at most 30 characters each (RoomSettingsCtrl:515-516, addTag at 1289-1301).
    private List<String> tags;
    private TradeMode tradeMode;
    private Boolean allowPets;
    private Boolean allowFoodConsume;
    private Boolean allowWalkThrough;
    // The client only lets VIP users change hideWalls, the thicknesses, leaveOnDoorTileEnabled and the
    // idle settings (RoomSettingsCtrl:1141-1171), and resends the stored values for other users.
    private Boolean hideWalls;
    private RoomThickness wallThickness;
    private RoomThickness floorThickness;
    // Never ALL for mute and ban: the client offers none, rights, and for group rooms group admins (RoomSettingsCtrl:867-869).
    private RoomModerationPermission whoCanMute;
    private RoomModerationPermission whoCanKick;
    private RoomModerationPermission whoCanBan;
    private ChatFloodSensitivity chatFloodSensitivity;
    private Boolean leaveOnDoorTileEnabled;
    private Boolean idleSleepEnabled;
    // 0 when switched off, else 30 to 3600 seconds (RoomSettingsCtrl:1143, 1247).
    private Integer idleSleepTimeoutSeconds;
    private Boolean idleAutokickEnabled;
    // 0 when switched off, else 60 to 36000 seconds (RoomSettingsCtrl:1145, 1252). When a VIP user turns
    // both on, the client also keeps it at least the sleep timeout + 30 (RoomSettingsCtrl:1257). That isn't
    // checked: other users resend the stored values, and nothing shows the server keeps that rule.
    private Integer idleAutokickTimeoutSeconds;
    private Boolean muteAllPets;

    public static SaveRoomSettings fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static SaveRoomSettings fromJson(String json) {
        return Json.parse(SaveRoomSettings.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
