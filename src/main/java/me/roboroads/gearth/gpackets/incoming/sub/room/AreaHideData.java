package me.roboroads.gearth.gpackets.incoming.sub.room;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * An area hide furni's hidden rectangle of floor, from the client's {@code AreaHideMessageData}.
 * The room engine turns it into a floor hole ({@code updateAreaHide}).
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class AreaHideData implements SubPacket, JsonSerializable {
    public static final Schema<AreaHideData> SCHEMA = Schema.of(AreaHideData.class)
            .integer("furniId")
            .bool("isOn")
            .integer("rootX")
            .integer("rootY")
            .integer("width")
            .integer("length")
            .bool("invert");

    private Integer furniId;
    // The client calls it on: true adds the floor hole, false removes it.
    private Boolean isOn;
    private Integer rootX;
    private Integer rootY;
    private Integer width;
    private Integer length;
    private Boolean invert;

    public static AreaHideData fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
