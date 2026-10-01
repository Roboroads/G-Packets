package me.roboroads.gearth.gpackets.incoming.sub.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.Gender;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.Unused;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class Player extends User {
    private Gender sex;
    private Integer groupId;
    // The client's parser reads it and RoomUsersHandler copies it into UserData, but nothing reads it there.
    @Unused("The client copies it into its user data but nothing reads it")
    @Deprecated
    private Integer groupStatus;
    private String groupName;
    private String swimFigure;
    private Integer achievementScore;
    private Boolean isModerator;
    // The infostand shows it as "Badge rank: #N" (infostand.text.badges_rank) and hides that line
    // when it is below 0. UserChange.badgesRank updates it.
    private Integer badgesRank;

    public Player(Integer id, String name, String custom, String figure, Integer roomIndex, Integer x, Integer y, String z, Direction dir, UserType type, Gender sex, Integer groupId, Integer groupStatus, String groupName, String swimFigure, Integer achievementScore, Boolean isModerator, Integer badgesRank) {
        super(id, name, custom, figure, roomIndex, x, y, z, dir, type);
        this.sex = sex;
        this.groupId = groupId;
        this.groupStatus = groupStatus;
        this.groupName = groupName;
        this.swimFigure = swimFigure;
        this.achievementScore = achievementScore;
        this.isModerator = isModerator;
        this.badgesRank = badgesRank;
    }

    public static Player fromJson(String json) {
        return Json.parse(Player.class, json);
    }
}
