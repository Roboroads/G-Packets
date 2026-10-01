package me.roboroads.gearth.gpackets.support.schema;

/**
 * An enum written to the wire as a string code. Lombok's fluent {@code @Getter} on a {@code code}
 * field implements it.
 */
public interface StringEnum {
    String code();
}
