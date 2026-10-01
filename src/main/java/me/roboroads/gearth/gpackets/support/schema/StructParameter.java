package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.support.schema.limit.Violation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** A nested structure, read inline. Its values sit in their own map. */
public final class StructParameter extends Parameter {
    private final Schema<?> schema;

    StructParameter(String name, Schema<?> schema) {
        super(name);
        this.schema = schema;
    }

    /** The nested structure's schema. */
    public Schema<?> schema() {
        return schema;
    }

    @Override
    void read(HPacket packet, Map<String, Object> values, String path) {
        Map<String, Object> nested = new LinkedHashMap<>();
        schema.readInto(packet, nested, path + "." + name());
        values.put(name(), nested);
    }

    @Override
    void write(Map<String, Object> values, HPacket packet, String path) {
        String here = path + "." + name();
        schema.writeFrom(asValues(values.get(name()), here), packet, here);
    }

    @Override
    void check(Map<String, Object> values, String path, List<Violation> out) {
        String here = at(path, name());
        schema.checkInto(asValues(values.get(name()), here), here, out);
    }
}
