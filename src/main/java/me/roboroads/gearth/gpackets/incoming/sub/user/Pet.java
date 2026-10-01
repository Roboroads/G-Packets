package me.roboroads.gearth.gpackets.incoming.sub.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.support.Json;

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
    Integer petLevel;
    String petPosture;

    public Pet(Integer id, String name, String custom, String figure, Integer roomIndex, Integer x, Integer y, String z, Direction dir, UserType type, Integer subType, Integer ownerId, String ownerName, Integer rarityLevel, Boolean hasSaddle, Boolean isRiding, Boolean canBreed, Boolean canHarvest, Boolean canRevive, Boolean hasBreedingPermission, Integer petLevel, String petPosture) {
        super(id, name, custom, figure, roomIndex, x, y, z, dir, type);
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
        this.petLevel = petLevel;
        this.petPosture = petPosture;
    }

    public static Pet fromJson(String json) {
        return Json.parse(Pet.class, json);
    }
}
