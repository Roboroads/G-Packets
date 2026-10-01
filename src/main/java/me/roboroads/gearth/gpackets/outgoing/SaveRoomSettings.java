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
    public static final PacketType<SaveRoomSettings> TYPE = PacketType.of("SaveRoomSettings", HMessage.Direction.TOSERVER, Schema.of(SaveRoomSettings.class)
            .integer("roomId")
            .string("name")
            .string("description")
            .enumInt("doorMode", DoorMode.class)
            .string("password")
            .enumInt("maximumVisitors", MaximumVisitors.class)
            .integer("categoryId")
            .list("tags", WireType.STRING)
            .enumInt("tradeMode", TradeMode.class)
            .bool("allowPets")
            .bool("allowFoodConsume")
            .bool("allowWalkThrough")
            .bool("hideWalls")
            .enumInt("wallThickness", RoomThickness.class)
            .enumInt("floorThickness", RoomThickness.class)
            .enumInt("whoCanMute", RoomModerationPermission.class)
            .enumInt("whoCanKick", RoomModerationPermission.class)
            .enumInt("whoCanBan", RoomModerationPermission.class)
            .enumInt("chatFloodSensitivity", ChatFloodSensitivity.class)
            .bool("leaveOnDoorTileEnabled")
            .bool("idleSleepEnabled")
            .integer("idleSleepTimeoutSeconds")
            .bool("idleAutokickEnabled")
            .integer("idleAutokickTimeoutSeconds")
            .bool("muteAllPets"));

    private Integer roomId;
    private String name;
    private String description;
    private DoorMode doorMode;
    // The client sends "" unless the door mode is password.
    private String password;
    private MaximumVisitors maximumVisitors;
    private Integer categoryId;
    private List<String> tags;
    private TradeMode tradeMode;
    private Boolean allowPets;
    private Boolean allowFoodConsume;
    private Boolean allowWalkThrough;
    private Boolean hideWalls;
    private RoomThickness wallThickness;
    private RoomThickness floorThickness;
    private RoomModerationPermission whoCanMute;
    private RoomModerationPermission whoCanKick;
    private RoomModerationPermission whoCanBan;
    private ChatFloodSensitivity chatFloodSensitivity;
    private Boolean leaveOnDoorTileEnabled;
    private Boolean idleSleepEnabled;
    private Integer idleSleepTimeoutSeconds;
    private Boolean idleAutokickEnabled;
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
