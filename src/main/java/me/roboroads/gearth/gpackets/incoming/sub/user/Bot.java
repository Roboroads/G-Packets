package me.roboroads.gearth.gpackets.incoming.sub.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.Gender;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;

import java.util.List;

@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class Bot extends User {
    private Gender sex;
    private Integer ownerId;
    private String ownerName;
    // The client calls it botSkills.
    private List<Short> skills;

    public Bot(Integer id, String name, String motto, String figure, Integer userIndex, Integer x, Integer y, String z, Direction bodyDirection, UserType type, Gender sex, Integer ownerId, String ownerName, List<Short> skills) {
        super(id, name, motto, figure, userIndex, x, y, z, bodyDirection, type);
        this.sex = sex;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.skills = skills;
    }

    public static Bot fromJson(String json) {
        return Json.parse(Bot.class, json);
    }
}
