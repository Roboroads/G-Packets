package testfixtures.discovery;

/** No @Intercept methods, so GPackets.init must never create it. */
public class NotAHandler {
    public static int created;

    public NotAHandler() {
        created++;
    }
}
