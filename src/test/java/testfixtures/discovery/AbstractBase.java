package testfixtures.discovery;

import me.roboroads.gearth.gpackets.Intercept;
import me.roboroads.gearth.gpackets.outgoing.Chat;

public abstract class AbstractBase {
    @Intercept
    void onChat(Chat chat) {
        Events.LOG.add(getClass().getSimpleName());
    }
}
