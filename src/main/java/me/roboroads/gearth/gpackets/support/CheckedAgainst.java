package me.roboroads.gearth.gpackets.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The client build a packet or sub-structure was last checked against, in the format the outgoing
 * {@code ClientHello} sends as {@code releaseVersion}: {@code "WIN63-202609091217-117204808"}.
 *
 * <p>Every packet and sub-structure carries one, and a subtype carries its own. After a client
 * update, the classes whose build isn't the newest one are the ones left to re-check; re-checking
 * one that didn't change only bumps the build.
 *
 * <p>Tools read it through {@code PacketType.checkedAgainst()} or from the class.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CheckedAgainst {
    /** The client build, for example "WIN63-202609091217-117204808". */
    String value();
}
