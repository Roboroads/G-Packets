package testfixtures.discovery;

import me.roboroads.gearth.gpackets.Intercept;
import me.roboroads.gearth.gpackets.outgoing.Chat;

public class NoArgHandler {
    @Intercept
    void onChat(Chat chat) {
        Events.LOG.add("NoArgHandler");
    }
}
