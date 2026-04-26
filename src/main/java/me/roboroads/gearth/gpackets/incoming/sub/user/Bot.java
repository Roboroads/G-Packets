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

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class Bot extends User {
    private Gender sex;
    private Integer ownerId;
    private String ownerName;
    private List<Short> botSkills;

    public Bot(Integer id, String name, String custom, String figure, Integer roomIndex, Integer x, Integer y, String z, Direction dir, UserType type, Gender sex, Integer ownerId, String ownerName, List<Short> botSkills) {
        super(id, name, custom, figure, roomIndex, x, y, z, dir, type);
        this.sex = sex;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.botSkills = botSkills;
    }

    public static Bot fromJson(String json) {
        return Json.parse(Bot.class, json);
    }

    @Override
    public void appendPacket(HPacket packet) {
        super.appendPacket(packet);
        packet.appendString(sex != null ? sex.code() : "");
        packet.appendInt(ownerId);
        packet.appendString(ownerName);
        packet.appendInt(botSkills != null ? botSkills.size() : 0);
        if (botSkills != null) {
            for (Short skill : botSkills) {
                packet.appendShort(skill);
            }
        }
    }
}
