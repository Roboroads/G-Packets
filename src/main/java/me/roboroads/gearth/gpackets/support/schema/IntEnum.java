package me.roboroads.gearth.gpackets.support.schema;

/**
 * An enum written to the wire as an int. Lombok's fluent {@code @Getter} on a {@code value} field
 * implements it.
 */
public interface IntEnum {
    int value();
}
