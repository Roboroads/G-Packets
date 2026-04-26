package me.roboroads.gearth.gpackets;

import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.support.Packet;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PacketImplementationTest {

    @Test
    public void verifyPacketImplementations() {
        Reflections reflections = new Reflections("me.roboroads.gearth.gpackets");
        Set<Class<? extends Packet>> packetClasses = reflections.getSubTypesOf(Packet.class);

        assertFalse(packetClasses.isEmpty(), "No Packet implementations found! Check scan package.");

        for (Class<? extends Packet> clazz : packetClasses) {
            // Skip interfaces and abstract classes
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            // Check fromPacket(HPacket)
            try {
                Method fromPacket = clazz.getDeclaredMethod("fromPacket", HPacket.class);
                assertTrue(Modifier.isStatic(fromPacket.getModifiers()),
                        "Method fromPacket in " + clazz.getName() + " must be static");
                assertTrue(clazz.isAssignableFrom(fromPacket.getReturnType()),
                        "Method fromPacket in " + clazz.getName() + " must return " + clazz.getSimpleName() + " (or subtype), but returns " + fromPacket.getReturnType().getSimpleName());
            } catch (NoSuchMethodException e) {
                fail("Class " + clazz.getName() + " must implement static method: public static " + clazz.getSimpleName() + " fromPacket(HPacket packet)");
            }

            // Check fromJson(String)
            try {
                Method fromJson = clazz.getDeclaredMethod("fromJson", String.class);
                assertTrue(Modifier.isStatic(fromJson.getModifiers()),
                        "Method fromJson in " + clazz.getName() + " must be static");
                assertTrue(clazz.isAssignableFrom(fromJson.getReturnType()),
                        "Method fromJson in " + clazz.getName() + " must return " + clazz.getSimpleName() + " (or subtype), but returns " + fromJson.getReturnType().getSimpleName());
            } catch (NoSuchMethodException e) {
                fail("Class " + clazz.getName() + " must implement static method: public static " + clazz.getSimpleName() + " fromJson(String json)");
            }

            // Check HEADER field
            try {
                java.lang.reflect.Field headerField = clazz.getDeclaredField("HEADER");
                assertTrue(Modifier.isPublic(headerField.getModifiers()), "HEADER field in " + clazz.getName() + " must be public");
                assertTrue(Modifier.isStatic(headerField.getModifiers()), "HEADER field in " + clazz.getName() + " must be static");
                assertTrue(Modifier.isFinal(headerField.getModifiers()), "HEADER field in " + clazz.getName() + " must be final");
                assertEquals(String.class, headerField.getType(), "HEADER field in " + clazz.getName() + " must be of type String");
            } catch (NoSuchFieldException e) {
                fail("Class " + clazz.getName() + " must have a public static final String HEADER field");
            }
        }
    }
}
