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
public class OldBot extends User {
    public OldBot(Integer id, String name, String custom, String figure, Integer roomIndex, Integer x, Integer y, String z, Direction dir, UserType type) {
        super(id, name, custom, figure, roomIndex, x, y, z, dir, type);
    }

    public static OldBot fromJson(String json) {
        return Json.parse(OldBot.class, json);
    }
}
