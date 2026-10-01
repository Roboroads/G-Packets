package testfixtures.scanning;

import gearth.extensions.FakeExtension;
import me.roboroads.gearth.gpackets.Intercept;
import me.roboroads.gearth.gpackets.outgoing.Chat;

/** An extension with its own handler, alone in its package so discovery finds nothing else. */
public class ScanningExtension extends FakeExtension {
    public String seen;

    @Intercept(Chat.class)
    void onChat(Chat chat) {
        seen = chat.text();
    }
}
