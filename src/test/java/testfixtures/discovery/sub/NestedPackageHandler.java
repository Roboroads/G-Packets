package testfixtures.discovery.sub;

import me.roboroads.gearth.gpackets.Intercept;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import testfixtures.discovery.Events;

public class NestedPackageHandler {
    @Intercept
    void onChat(Chat chat) {
        Events.LOG.add("sub.NestedPackageHandler");
    }
}
