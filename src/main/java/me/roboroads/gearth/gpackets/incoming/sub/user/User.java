package me.roboroads.gearth.gpackets.incoming.sub.user;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.Gender;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

// Jackson polymorphic config: use existing integer "type" field to pick subtype
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes(
  {@JsonSubTypes.Type(value = Player.class, name = "1"), @JsonSubTypes.Type(value = Pet.class, name = "2"), @JsonSubTypes.Type(value = OldBot.class, name = "3"), @JsonSubTypes.Type(value = Bot.class, name = "4")}
)
@Data
@AllArgsConstructor
@SuperBuilder
public abstract class User implements SubPacket, JsonSerializable {
    public static final Schema<User> SCHEMA = Schema.of(User.class)
            .integer("id")
            .string("name")
            .string("custom")
            .string("figure")
            .integer("roomIndex")
            .integer("x")
            .integer("y")
            .string("z")
            .enumInt("dir", Direction.class)
            .enumInt("type", UserType.class)
            .branch("type", cases -> cases
                    .on(UserType.PLAYER, Player.class, s -> s
                            .enumString("sex", Gender.class)
                            .integer("groupId")
                            .integer("groupStatus")
                            .string("groupName")
                            .string("swimFigure")
                            .integer("achievementScore")
                            .bool("isModerator"))
                    .on(UserType.PET, Pet.class, s -> s
                            .integer("subType")
                            .integer("ownerId")
                            .string("ownerName")
                            .integer("rarityLevel")
                            .bool("hasSaddle")
                            .bool("isRiding")
                            .bool("canBreed")
                            .bool("canHarvest")
                            .bool("canRevive")
                            .bool("hasBreedingPermission")
                            .integer("petLevel")
                            .string("petPosture"))
                    .on(UserType.OLD_BOT, OldBot.class, s -> s)
                    .on(UserType.BOT, Bot.class, s -> s
                            .enumString("sex", Gender.class)
                            .integer("ownerId")
                            .string("ownerName")
                            .list("botSkills", WireType.SHORT)));

    private Integer id;
    private String name;
    private String custom;
    private String figure;
    private Integer roomIndex;
    private Integer x;
    private Integer y;
    private String z;
    private Direction dir;
    private UserType type;

    public static User fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
