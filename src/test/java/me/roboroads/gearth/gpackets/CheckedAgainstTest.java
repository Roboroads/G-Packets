package me.roboroads.gearth.gpackets;

import me.roboroads.gearth.gpackets.outgoing.Chat;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.SubPacket;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Inherited;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Every packet and sub-structure says which client build it was checked against. */
class CheckedAgainstTest {

    // Packets and sub-structures live under incoming and outgoing; test fixtures live elsewhere.
    private static final Reflections REFLECTIONS = new Reflections(new ConfigurationBuilder()
            .forPackage("me.roboroads.gearth.gpackets")
            .filterInputsBy(new FilterBuilder()
                    .includePackage("me.roboroads.gearth.gpackets.incoming")
                    .includePackage("me.roboroads.gearth.gpackets.outgoing"))
            .setScanners(Scanners.SubTypes));

    // The format ClientHello sends as releaseVersion: platform, build time, build number.
    private static final Pattern CLIENT_BUILD = Pattern.compile("[A-Z0-9]+-\\d{12}-\\d+");

    @Test
    void everyPacketAndSubStructureHasAClientBuild() {
        Set<Class<?>> classes = new TreeSet<>((a, b) -> a.getName().compareTo(b.getName()));
        classes.addAll(REFLECTIONS.getSubTypesOf(Packet.class));
        classes.addAll(REFLECTIONS.getSubTypesOf(SubPacket.class));
        classes.removeIf(Class::isInterface);
        assertTrue(classes.size() > 100, "Found only " + classes.size() + " classes; check the scan packages");

        List<String> problems = new ArrayList<>();
        for (Class<?> type : classes) {
            // Not inherited: a subtype such as Player carries its own.
            CheckedAgainst checked = type.getDeclaredAnnotation(CheckedAgainst.class);
            if (checked == null) {
                problems.add(type.getName() + " has no @CheckedAgainst");
            } else if (!CLIENT_BUILD.matcher(checked.value()).matches()) {
                problems.add(type.getName() + ": \"" + checked.value() + "\" is not a client build");
            }
        }
        assertEquals(new ArrayList<String>(), problems);
    }

    @Test
    void aSubtypeDoesNotInheritItsParentsBuild() {
        assertFalse(CheckedAgainst.class.isAnnotationPresent(Inherited.class));
    }

    @Test
    void packetTypeReadsTheBuildFromTheClass() {
        assertEquals(Chat.class.getAnnotation(CheckedAgainst.class).value(), Chat.TYPE.checkedAgainst());
        assertTrue(CLIENT_BUILD.matcher(Chat.TYPE.checkedAgainst()).matches());
    }
}
