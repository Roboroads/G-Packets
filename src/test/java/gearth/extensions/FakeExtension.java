package gearth.extensions;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import gearth.services.packet_info.PacketInfoManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Test double for {@link IExtension}. It lives in {@code gearth.extensions} because IExtension has
 * package-private abstract methods. It records every {@code intercept(direction, header, listener)}
 * call and can {@link #fire} a packet to run the matching recorded listeners.
 */
public class FakeExtension extends IExtension {

    public static final class Registration {
        public final HMessage.Direction direction;
        public final String header;
        public final ExtensionBase.MessageListener listener;

        Registration(HMessage.Direction direction, String header, ExtensionBase.MessageListener listener) {
            this.direction = direction;
            this.header = header;
            this.listener = listener;
        }
    }

    public final List<Registration> registrations = new ArrayList<>();

    /** Runs every listener registered for the packet's header and the given direction. */
    public HMessage fire(HPacket packet, HMessage.Direction direction) {
        String header = packet.packetIncompleteIdentifier();
        HMessage message = new HMessage(packet, direction, 0);
        for (Registration registration : registrations) {
            if (registration.direction == direction && header != null && header.equals(registration.header)) {
                registration.listener.act(message);
            }
        }
        return message;
    }

    @Override
    public void intercept(HMessage.Direction direction, String header, ExtensionBase.MessageListener messageListener) {
        registrations.add(new Registration(direction, header, messageListener));
    }

    @Override
    public void intercept(HMessage.Direction direction, int headerId, ExtensionBase.MessageListener messageListener) {
        // not used by these tests
    }

    @Override
    public void intercept(HMessage.Direction direction, ExtensionBase.MessageListener messageListener) {
        // not used by these tests
    }

    @Override
    public boolean sendToClient(HPacket packet) {
        return true;
    }

    @Override
    public boolean sendToServer(HPacket packet) {
        return true;
    }

    @Override
    public boolean requestFlags(ExtensionBase.FlagsCheckListener flagsCheckListener) {
        return false;
    }

    @Override
    public void writeToConsole(String s) {
    }

    @Override
    public void writeToConsole(String s, String s1) {
    }

    @Override
    public void onConnect(OnConnectionListener onConnectionListener) {
    }

    @Override
    void initExtension() {
    }

    @Override
    void onClick() {
    }

    @Override
    void onStartConnection() {
    }

    @Override
    void onEndConnection() {
    }

    @Override
    ExtensionInfo getInfoAnnotations() {
        return null;
    }

    @Override
    boolean canLeave() {
        return true;
    }

    @Override
    boolean canDelete() {
        return true;
    }

    @Override
    public PacketInfoManager getPacketInfoManager() {
        return null;
    }
}
