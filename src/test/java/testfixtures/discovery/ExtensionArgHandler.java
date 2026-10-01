package testfixtures.discovery;

import me.roboroads.gearth.gpackets.Intercept;
import me.roboroads.gearth.gpackets.outgoing.Chat;

public class ExtensionArgHandler {
    public static DiscoveryExtension received;

    public ExtensionArgHandler(DiscoveryExtension extension) {
        received = extension;
    }

    @Intercept
    void onChat(Chat chat) {
        Events.LOG.add("ExtensionArgHandler");
    }
}
