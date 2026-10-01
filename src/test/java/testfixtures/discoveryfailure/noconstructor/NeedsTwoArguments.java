package testfixtures.discoveryfailure.noconstructor;

import me.roboroads.gearth.gpackets.Intercept;
import me.roboroads.gearth.gpackets.outgoing.Chat;

/** Its only constructor needs arguments GPackets.init can't supply. */
public class NeedsTwoArguments {
    public NeedsTwoArguments(String name, int count) {
    }

    @Intercept
    void onChat(Chat chat) {
    }
}
