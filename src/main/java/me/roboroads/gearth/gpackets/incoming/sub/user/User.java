package me.roboroads.gearth.gpackets.incoming.sub.user;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.Utils;

import java.util.Collections;
import java.util.List;

// Jackson polymorphic config: use existing integer "type" field to pick subtype
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes(
  {@JsonSubTypes.Type(value = Player.class, name = "1"), @JsonSubTypes.Type(value = Pet.class, name = "2"), @JsonSubTypes.Type(value = OldBot.class, name = "3"), @JsonSubTypes.Type(value = Bot.class, name = "4")}
)
@Data
@AllArgsConstructor
@SuperBuilder
public abstract class User implements SubPacket, JsonSerializable {
    private Integer id;
    private String name;
    private String custom;
    private String figure;
    private Integer roomIndex;
    private Integer x;
    private Integer y;
    private String z;
    private Integer dir;
    private Integer type;

    public static User fromPacket(HPacket packet) {
        Integer id = packet.readInteger();
        String name = packet.readString();
        String custom = packet.readString();
        String figure = packet.readString();
        Integer roomIndex = packet.readInteger();
        Integer x = packet.readInteger();
        Integer y = packet.readInteger();
        String z = packet.readString();
        Integer dir = packet.readInteger();
        int type = packet.readInteger();

        switch (type) {
            case 1: {
                String sex = packet.readString();
                Integer groupId = packet.readInteger();
                Integer groupStatus = packet.readInteger();
                String groupName = packet.readString();
                String swimFigure = packet.readString();
                Integer achievementScore = packet.readInteger();
                Boolean isModerator = packet.readBoolean();
                return new Player(id, name, custom, figure, roomIndex, x, y, z, dir, type, sex, groupId, groupStatus, groupName, swimFigure, achievementScore, isModerator);
            }
            case 2: {
                Integer subType = packet.readInteger();
                Integer ownerId = packet.readInteger();
                String ownerName = packet.readString();
                Integer rarityLevel = packet.readInteger();
                Boolean hasSaddle = packet.readBoolean();
                Boolean isRiding = packet.readBoolean();
                Boolean canBreed = packet.readBoolean();
                Boolean canHarvest = packet.readBoolean();
                Boolean canRevive = packet.readBoolean();
                Boolean hasBreedingPermission = packet.readBoolean();
                Integer petLevel = packet.readInteger();
                String petPosture = packet.readString();
                return new Pet(id, name, custom, figure, roomIndex, x, y, z, dir, type, subType, ownerId, ownerName, rarityLevel, hasSaddle, isRiding, canBreed, canHarvest, canRevive, hasBreedingPermission, petLevel, petPosture);
            }
            case 3: {
                return new OldBot(id, name, custom, figure, roomIndex, x, y, z, dir, type);
            }
            case 4: {
                String sex = packet.readString();
                Integer ownerId = packet.readInteger();
                String ownerName = packet.readString();
                List<Short> botSkills = Utils.readList(packet, HPacket::readShort);
                return new Bot(id, name, custom, figure, roomIndex, x, y, z, dir, type, sex, ownerId, ownerName, Collections.unmodifiableList(botSkills));
            }
            default:
                throw new IllegalArgumentException("Unknown user type: " + type);
        }
    }

    public void appendPacket(HPacket packet) {
        packet.appendInt(id);
        packet.appendString(name);
        packet.appendString(custom);
        packet.appendString(figure);
        packet.appendInt(roomIndex);
        packet.appendInt(x);
        packet.appendInt(y);
        packet.appendString(z);
        packet.appendInt(dir);
        packet.appendInt(type);
    }
}

