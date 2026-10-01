package testfixtures.discovery;

import me.roboroads.gearth.gpackets.Intercept;
import me.roboroads.gearth.gpackets.outgoing.Chat;

/** Tests pass an instance of this one to GPackets.init, so it must not be created again. */
public class PassedHandler {
    public static int created;

    public PassedHandler() {
        created++;
    }

    @Intercept
    void onChat(Chat chat) {
        Events.LOG.add("PassedHandler");
    }
}
