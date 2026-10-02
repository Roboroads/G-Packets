package me.roboroads.gearth.gpackets.incoming.sub.furni;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.function.UnaryOperator;

/**
 * A furni's state data: what a furni shows or does beyond its type, such as a state string, a
 * map of values or a high score table. The low byte of {@link #typeAndFlags} picks the format and
 * so the subclass; {@link #UNIQUE_SERIAL_FLAG} adds the serial number of a limited edition.
 */
// The client: parseStuffData, and getStuffDataWrapperForType in room/object/data.
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "typeAndFlags", visible = true)
@JsonTypeIdResolver(StuffDataTypeIdResolver.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class StuffData implements SubPacket, JsonSerializable {
    /** The bits of {@link #typeAndFlags} that hold the format: {@code typeAndFlags & FORMAT_MASK}. */
    public static final int FORMAT_MASK = 0xFF;

    /**
     * The flag in {@link #typeAndFlags} for a limited edition furni: {@link #uniqueSerialNumber} and
     * {@link #uniqueSeriesSize} follow the format's values. The client calls it UNIQUE_SERIAL_NUMBER.
     */
    public static final int UNIQUE_SERIAL_FLAG = 256;

    // The client picks the format from the low byte and only checks UNIQUE_SERIAL_FLAG of the other
    // bits, so any other flag passes through as part of typeAndFlags.
    public static final Schema<StuffData> SCHEMA = Schema.of(StuffData.class)
            .integer("typeAndFlags")
            .branch("typeAndFlags", FORMAT_MASK, cases -> cases
                    .on(0, LegacyStuffData.class, withSerial(s -> s
                            .string("legacyString")))
                    .on(1, MapStuffData.class, withSerial(s -> s
                            .list("entries", MapStuffDataEntry.SCHEMA)))
                    .on(2, StringArrayStuffData.class, withSerial(s -> s
                            .list("values", WireType.STRING)))
                    .on(3, VoteResultStuffData.class, withSerial(s -> s
                            .string("legacyString")
                            .integer("result")))
                    .on(4, EmptyStuffData.class, withSerial(s -> s))
                    .on(5, IntArrayStuffData.class, withSerial(s -> s
                            .list("values", WireType.INT)))
                    // The client's high score data never reads the serial, even with the flag.
                    .on(6, HighScoreStuffData.class, s -> s
                            .string("legacyString")
                            .integer("scoreType")
                            .integer("clearType")
                            .list("entries", HighScoreData.SCHEMA))
                    .on(7, CrackableStuffData.class, withSerial(s -> s
                            .string("legacyString")
                            .integer("hits")
                            .integer("target"))));

    // The format in the low byte (0 legacy, 1 map, 2 string array, 3 vote result, 4 empty, 5 int
    // array, 6 high score, 7 crackable), plus UNIQUE_SERIAL_FLAG for a limited edition. Set it to
    // write a serial: without the flag, the serial isn't on the wire.
    private Integer typeAndFlags;
    // The edition number of a limited edition furni. Only on the wire with UNIQUE_SERIAL_FLAG.
    private Integer uniqueSerialNumber;
    // How many of this limited edition furni exist. Only on the wire with UNIQUE_SERIAL_FLAG.
    private Integer uniqueSeriesSize;

    public static StuffData fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }

    private static <S extends StuffData> UnaryOperator<Schema<S>> withSerial(UnaryOperator<Schema<S>> body) {
        return s -> body.apply(s)
                .when("typeAndFlags", UNIQUE_SERIAL_FLAG, UNIQUE_SERIAL_FLAG, serial -> serial
                        .integer("uniqueSerialNumber")
                        .integer("uniqueSeriesSize"));
    }
}
