package me.roboroads.gearth.gpackets;

import me.roboroads.gearth.gpackets.support.Packet;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a packet handler. {@link GPackets#init} registers every annotated method
 * through G-Earth's own {@code intercept(...)}.
 *
 * <p>The method returns {@code void}, is not static, and takes the packet first with an optional
 * trailing {@code HMessage}. When more than one packet class is listed, the packet parameter must
 * accept all of them (use {@link Packet}).
 *
 * <p>With no value ({@code @Intercept}), the packet type is inferred from the method's first
 * parameter, so {@code @Intercept void onUsers(Users users)} handles {@code Users}. Inference needs
 * a concrete packet parameter; a method that takes only {@code HMessage}, or takes {@link Packet}
 * itself, must list its packet classes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Intercept {
    Class<? extends Packet>[] value() default {};
}
