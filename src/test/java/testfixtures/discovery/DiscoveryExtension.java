package testfixtures.discovery;

import gearth.extensions.FakeExtension;
import me.roboroads.gearth.gpackets.Intercept;
import me.roboroads.gearth.gpackets.outgoing.Chat;

/** An extension whose package holds handler classes for GPackets.init to find. */
public class DiscoveryExtension extends FakeExtension {
    @Intercept
    void onChat(Chat chat) {
        Events.LOG.add("DiscoveryExtension");
    }
}
