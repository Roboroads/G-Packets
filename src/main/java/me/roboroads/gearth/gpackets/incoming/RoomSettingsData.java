package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.room.RoomModerationSettings;
import me.roboroads.gearth.gpackets.model.enums.ChatFloodSensitivity;
import me.roboroads.gearth.gpackets.model.enums.DoorMode;
import me.roboroads.gearth.gpackets.model.enums.RoomThickness;
import me.roboroads.gearth.gpackets.model.enums.TradeMode;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

/** The settings of a room you can edit, the answer to {@code GetRoomSettings}. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomSettingsData implements Packet, JsonSerializable {
    public static final PacketType<RoomSettingsData> TYPE = PacketType.of("RoomSettingsData", HMessage.Direction.TOCLIENT, Schema.of(RoomSettingsData.class)
            .integer("roomId")
            .string("name")
            .string("description")
            .enumInt("doorMode", DoorMode.class)
            .integer("categoryId")
            .integer("maximumVisitors")
            .integer("maximumVisitorsLimit")
            .list("tags", WireType.STRING)
            .enumInt("tradeMode", TradeMode.class)
            .integer("allowPets")
            .integer("allowFoodConsume")
            .integer("allowWalkThrough")
            .integer("hideWalls")
            .enumInt("wallThickness", RoomThickness.class)
            .enumInt("floorThickness", RoomThickness.class)
            .enumInt("chatFloodSensitivity", ChatFloodSensitivity.class)
            .bool("leaveOnDoorTileEnabled")
            .bool("idleSleepEnabled")
            .integer("idleSleepTimeoutSeconds")
            .bool("idleAutokickEnabled")
            .integer("idleAutokickTimeoutSeconds")
            .bool("muteAllPets")
            .struct("roomModerationSettings", RoomModerationSettings.SCHEMA)
            .bool("hiddenByBc"));

    private Integer roomId;
    private String name;
    private String description;
    private DoorMode doorMode;
    private Integer categoryId;
    private Integer maximumVisitors;
    private Integer maximumVisitorsLimit;
    private List<String> tags;
    private TradeMode tradeMode;
    // The four flags below are sent as ints: 1 is on, 0 is off. SaveRoomSettings sends them as booleans.
    private Integer allowPets;
    private Integer allowFoodConsume;
    private Integer allowWalkThrough;
    private Integer hideWalls;
    private RoomThickness wallThickness;
    private RoomThickness floorThickness;
    // The client builds its chat settings from this alone (fromFloodSensitivity).
    private ChatFloodSensitivity chatFloodSensitivity;
    private Boolean leaveOnDoorTileEnabled;
    private Boolean idleSleepEnabled;
    private Integer idleSleepTimeoutSeconds;
    private Boolean idleAutokickEnabled;
    private Integer idleAutokickTimeoutSeconds;
    private Boolean muteAllPets;
    private RoomModerationSettings roomModerationSettings;
    // The client shows doormode_override_info when this is set.
    private Boolean hiddenByBc;

    public static RoomSettingsData fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RoomSettingsData fromJson(String json) {
        return Json.parse(RoomSettingsData.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
