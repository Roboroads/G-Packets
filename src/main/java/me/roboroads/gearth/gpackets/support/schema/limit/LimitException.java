package me.roboroads.gearth.gpackets.support.schema.limit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Thrown when an outgoing packet breaks one or more limits or rules. Lists all of them. */
public class LimitException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    private final List<Violation> violations;

    public LimitException(String packet, List<Violation> violations) {
        super(message(packet, violations));
        this.violations = Collections.unmodifiableList(new ArrayList<>(violations));
    }

    /** Every broken limit and rule, in the order of the packet's parameters. */
    public List<Violation> violations() {
        return violations;
    }

    private static String message(String packet, List<Violation> violations) {
        StringBuilder out = new StringBuilder(packet).append(" breaks ").append(violations.size())
                .append(violations.size() == 1 ? " limit" : " limits")
                .append(" (use toPacketUnchecked() to send it anyway):");
        for (Violation violation : violations) {
            out.append("\n  ").append(violation);
        }
        return out.toString();
    }
}
