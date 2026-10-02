package me.roboroads.gearth.gpackets.incoming.sub.furni;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

/** One row of a {@link HighScoreStuffData}: a score and the users who got it. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class HighScoreData implements SubPacket, JsonSerializable {
    public static final Schema<HighScoreData> SCHEMA = Schema.of(HighScoreData.class)
            .integer("score")
            .list("users", WireType.STRING);

    private Integer score;
    private List<String> users;

    public static HighScoreData fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
