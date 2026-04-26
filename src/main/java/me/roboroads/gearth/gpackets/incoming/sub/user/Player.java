package me.roboroads.gearth.gpackets.incoming.sub.user;

import gearth.protocol.HPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.Gender;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.support.Json;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class Player extends User {
    private Gender sex;
    private Integer groupId;
    private Integer groupStatus;
    private String groupName;
    private String swimFigure;
    private Integer achievementScore;
    private Boolean isModerator;

    public Player(Integer id, String name, String custom, String figure, Integer roomIndex, Integer x, Integer y, String z, Direction dir, UserType type, Gender sex, Integer groupId, Integer groupStatus, String groupName, String swimFigure, Integer achievementScore, Boolean isModerator) {
        super(id, name, custom, figure, roomIndex, x, y, z, dir, type);
        this.sex = sex;
        this.groupId = groupId;
        this.groupStatus = groupStatus;
        this.groupName = groupName;
        this.swimFigure = swimFigure;
        this.achievementScore = achievementScore;
        this.isModerator = isModerator;
    }

    public static Player fromJson(String json) {
        return Json.parse(Player.class, json);
    }

    @Override
    public void appendPacket(HPacket packet) {
        super.appendPacket(packet);
        packet.appendString(sex != null ? sex.code() : "");
        packet.appendInt(groupId);
        packet.appendInt(groupStatus);
        packet.appendString(groupName);
        packet.appendString(swimFigure);
        packet.appendInt(achievementScore);
        packet.appendBoolean(isModerator);
    }
}
