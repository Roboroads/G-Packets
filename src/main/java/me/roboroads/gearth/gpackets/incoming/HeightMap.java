package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

/** The stacking height of every tile in the room, which tells the client where furni can go. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class HeightMap implements Packet, JsonSerializable {
    public static final PacketType<HeightMap> TYPE = PacketType.of("HeightMap", HMessage.Direction.TOCLIENT, Schema.of(HeightMap.class)
            .integer("width")
            .list("tiles", WireType.SHORT));

    private Integer width;
    // Row by row, width tiles per row; the client works out the height as tiles.size() / width. Each
    // tile is encoded: -1 is no tile, the stacking-blocked bit (bit 14 by default, the hotel setting
    // room.stacking_blocked_mask_bit) blocks stacking, and the bits below it are the stacking height
    // times 256 (the parser's decodeTileHeight).
    private List<Short> tiles;

    public static HeightMap fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static HeightMap fromJson(String json) {
        return Json.parse(HeightMap.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
