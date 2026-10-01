package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HPacket;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** An int count followed by that many elements, each a primitive or a structure. */
public final class ListParameter extends Parameter {
    private final WireType elementType;
    private final Supplier<? extends Schema<?>> elementSchema;

    ListParameter(String name, WireType elementType, Supplier<? extends Schema<?>> elementSchema) {
        super(name);
        this.elementType = elementType;
        this.elementSchema = elementSchema;
    }

    /** The element type of a list of primitives, or null for a list of structures. */
    public WireType elementType() {
        return elementType;
    }

    /** The element schema of a list of structures, or null for a list of primitives. */
    public Schema<?> elementSchema() {
        return elementSchema == null ? null : elementSchema.get();
    }

    @Override
    void read(HPacket packet, Map<String, Object> values, String path) {
        String here = path + "." + name();
        int count = (Integer) readWire(WireType.INT, packet, here);
        // No capacity hint: a garbage count must fail on the first missing element, not allocate.
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String at = here + "[" + i + "]";
            if (elementType != null) {
                list.add(readWire(elementType, packet, at));
            } else {
                Map<String, Object> element = new LinkedHashMap<>();
                elementSchema().readInto(packet, element, at);
                list.add(element);
            }
        }
        values.put(name(), list);
    }

    @Override
    void write(Map<String, Object> values, HPacket packet, String path) {
        String here = path + "." + name();
        Object value = values.get(name());
        if (value == null) {
            packet.appendInt(0);
            return;
        }
        if (!(value instanceof List)) {
            throw new IllegalArgumentException(here + ": expected a List, got " + value.getClass().getName());
        }
        List<?> list = (List<?>) value;
        packet.appendInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            String at = here + "[" + i + "]";
            if (elementType != null) {
                Object element;
                try {
                    element = elementType.coerce(list.get(i));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(at + ": " + e.getMessage());
                }
                elementType.write(packet, element);
            } else {
                elementSchema().writeFrom(asValues(list.get(i), at), packet, at);
            }
        }
    }
}
