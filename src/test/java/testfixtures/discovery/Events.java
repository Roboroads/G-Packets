package testfixtures.discovery;

import java.util.ArrayList;
import java.util.List;

/** What the discovery fixtures saw, in order. Tests clear it before each run. */
public final class Events {
    public static final List<String> LOG = new ArrayList<>();

    private Events() {
    }
}
