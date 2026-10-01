package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testfixtures.discovery.DiscoveryExtension;
import testfixtures.discovery.Events;
import testfixtures.discovery.ExtensionArgHandler;
import testfixtures.discovery.NotAHandler;
import testfixtures.discovery.PassedHandler;
import testfixtures.discoveryfailure.noconstructor.NoConstructorExtension;
import testfixtures.discoveryfailure.throwing.ThrowingExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HandlerDiscoveryTest {

    @BeforeEach
    void reset() {
        Events.LOG.clear();
        NotAHandler.created = 0;
        PassedHandler.created = 0;
        ExtensionArgHandler.received = null;
    }

    private static HPacket chat() {
        return new Chat("hi", ChatBarStyle.DEFAULT.value(), -1).toPacket();
    }

    @Test
    void initRegistersFoundHandlersAfterTheExtensionAndPassedOnes() {
        DiscoveryExtension extension = new DiscoveryExtension();

        GPackets.init(extension, new PassedHandler());
        extension.fire(chat(), HMessage.Direction.TOSERVER);

        assertEquals(Arrays.asList(
                "DiscoveryExtension",
                "PassedHandler",
                "ConcreteChild",
                "ExtensionArgHandler",
                "NoArgHandler",
                "sub.NestedPackageHandler"), Events.LOG);
    }

    @Test
    void aPassedHandlerIsNotCreatedAgain() {
        GPackets.init(new DiscoveryExtension(), new PassedHandler());

        assertEquals(1, PassedHandler.created);
    }

    @Test
    void aFoundHandlerGetsTheExtension() {
        DiscoveryExtension extension = new DiscoveryExtension();

        GPackets.init(extension);

        assertSame(extension, ExtensionArgHandler.received);
    }

    @Test
    void classesWithoutInterceptMethodsAreNotCreated() {
        GPackets.init(new DiscoveryExtension());

        assertEquals(0, NotAHandler.created);
    }

    @Test
    void aHandlerWithoutAUsableConstructorFailsInit() {
        NoConstructorExtension extension = new NoConstructorExtension();

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> GPackets.init(extension));

        assertTrue(e.getMessage().startsWith("testfixtures.discoveryfailure.noconstructor.NeedsTwoArguments: "), e.getMessage());
        assertTrue(extension.registrations.isEmpty());
    }

    @Test
    void aHandlerWhoseConstructorThrowsFailsInit() {
        ThrowingExtension extension = new ThrowingExtension();

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> GPackets.init(extension));

        assertEquals("boom", e.getCause().getMessage());
        assertTrue(extension.registrations.isEmpty());
    }
}
