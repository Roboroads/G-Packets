package testfixtures.discoveryfailure.throwing;

import me.roboroads.gearth.gpackets.Intercept;
import me.roboroads.gearth.gpackets.outgoing.Chat;

public class ThrowingHandler {
    public ThrowingHandler() {
        throw new IllegalStateException("boom");
    }

    @Intercept
    void onChat(Chat chat) {
    }
}
