package me.roboroads.gearth.gpackets.incoming.sub.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;

@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class Pet extends User {
    Integer subType;
    Integer ownerId;
    String ownerName;
    Integer rarityLevel;
    Boolean hasSaddle;
    Boolean isRiding;
    Boolean canBreed;
    Boolean canHarvest;
    Boolean canRevive;
    Boolean hasBreedingPermission;
    // The client calls it petLevel.
    Integer level;
    // The client calls it petPosture.
    String posture;

    public Pet(Integer id, String name, String motto, String figure, Integer userIndex, Integer x, Integer y, String z, Direction bodyDirection, UserType type, Integer subType, Integer ownerId, String ownerName, Integer rarityLevel, Boolean hasSaddle, Boolean isRiding, Boolean canBreed, Boolean canHarvest, Boolean canRevive, Boolean hasBreedingPermission, Integer level, String posture) {
        super(id, name, motto, figure, userIndex, x, y, z, bodyDirection, type);
        this.subType = subType;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.rarityLevel = rarityLevel;
        this.hasSaddle = hasSaddle;
        this.isRiding = isRiding;
        this.canBreed = canBreed;
        this.canHarvest = canHarvest;
        this.canRevive = canRevive;
        this.hasBreedingPermission = hasBreedingPermission;
        this.level = level;
        this.posture = posture;
    }

    public static Pet fromJson(String json) {
        return Json.parse(Pet.class, json);
    }
}
