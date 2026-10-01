package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.support.schema.limit.Violation;

import java.util.List;
import java.util.Map;

/**
 * Parameters at the end of a packet that the server may leave out. They are read only if bytes
 * remain, and written only if any of their values is set. Their values sit in the enclosing map.
 */
public final class OptionalParameter extends Parameter {
    private final Schema<?> schema;

    OptionalParameter(Schema<?> schema) {
        super(null);
        this.schema = schema;
    }

    /** The optional parameters. */
    public Schema<?> schema() {
        return schema;
    }

    @Override
    void read(HPacket packet, Map<String, Object> values, String path) {
        if (packet.isEOF() == 0) {
            schema.readInto(packet, values, path);
        }
    }

    @Override
    void write(Map<String, Object> values, HPacket packet, String path) {
        if (anySet(schema, values)) {
            schema.writeFrom(values, packet, path);
        }
    }

    @Override
    void check(Map<String, Object> values, String path, List<Violation> out) {
        if (anySet(schema, values)) {
            schema.checkInto(values, path, out);
        }
    }

    private static boolean anySet(Schema<?> schema, Map<String, Object> values) {
        for (Parameter parameter : schema.parameters()) {
            if (parameter instanceof BranchParameter) {
                for (BranchParameter.Case c : ((BranchParameter) parameter).cases().values()) {
                    if (anySet(c.schema(), values)) {
                        return true;
                    }
                }
            } else if (parameter instanceof OptionalParameter) {
                if (anySet(((OptionalParameter) parameter).schema(), values)) {
                    return true;
                }
            } else if (values.get(parameter.name()) != null) {
                return true;
            }
        }
        return false;
    }
}
