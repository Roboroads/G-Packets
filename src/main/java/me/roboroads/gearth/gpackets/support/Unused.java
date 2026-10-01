package me.roboroads.gearth.gpackets.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a parameter, enum value or packet that is on the wire but that the current Habbo client
 * ignores: it stores the value without reading it, declares the enum value without acting on it,
 * or registers the packet without handling it. G-Packets keeps these so packets still parse and
 * write in full.
 *
 * <p>Always paired with {@link Deprecated}, which makes the compiler and IDE warn wherever an
 * extension uses the element; Lombok copies it onto the generated getter, setter and builder
 * method. Here the deprecation means "the client ignores this", not "this will be removed".
 * Silence a deliberate use with {@code @SuppressWarnings("deprecation")}.
 *
 * <p>Tools read the reason through {@code Parameter.unused()}, {@code PacketType.unused()} and
 * {@code ValueParameter.unusedOptions()}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Unused {
    /** Why the client ignores it, for example "The client stores it but never reads it". */
    String value();
}
