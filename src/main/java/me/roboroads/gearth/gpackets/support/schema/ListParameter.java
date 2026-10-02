package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.support.schema.limit.Each;
import me.roboroads.gearth.gpackets.support.schema.limit.Limit;
import me.roboroads.gearth.gpackets.support.schema.limit.Violation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** A count (an int, unless {@link #countType()} says otherwise) followed by that many elements, each a primitive or a structure. */
public final class ListParameter extends Parameter {
    private final WireType countType;
    private final WireType elementType;
    private final Supplier<? extends Schema<?>> elementSchema;

    ListParameter(String name, WireType countType, WireType elementType, Supplier<? extends Schema<?>> elementSchema) {
        super(name);
        if (countType != WireType.INT && countType != WireType.SHORT && countType != WireType.BYTE) {
            throw new IllegalArgumentException(name + ": a list count is a BYTE, SHORT or INT, not " + countType);
        }
        this.countType = countType;
        this.elementType = elementType;
        this.elementSchema = elementSchema;
    }

    /** The wire type of the count before the elements: {@code INT} for most lists, {@code SHORT} or {@code BYTE} for a few. */
    public WireType countType() {
        return countType;
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
        int count = ((Number) readWire(countType, packet, here)).intValue();
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
            countType.write(packet, countType.defaultValue());
            return;
        }
        if (!(value instanceof List)) {
            throw new IllegalArgumentException(here + ": expected a List, got " + value.getClass().getName());
        }
        List<?> list = (List<?>) value;
        if (list.size() > maxCount()) {
            throw new IllegalArgumentException(here + ": " + list.size() + " elements don't fit a " + countType + " count");
        }
        countType.write(packet, countType.coerce(list.size()));
        // Iterate rather than get(i), which costs O(n) per call on a LinkedList.
        int i = 0;
        for (Object item : list) {
            String at = here + "[" + i++ + "]";
            if (elementType != null) {
                elementType.write(packet, coerceElement(item, at));
            } else {
                elementSchema().writeFrom(asValues(item, at), packet, at);
            }
        }
    }

    @Override
    void check(Map<String, Object> values, String path, List<Violation> out) {
        String here = at(path, name());
        Object value = values.get(name());
        if (value != null && !(value instanceof List)) {
            throw new IllegalArgumentException(here + ": expected a List, got " + value.getClass().getName());
        }
        List<?> list = value == null ? Collections.emptyList() : (List<?>) value;
        for (Limit limit : limits()) {
            if (!limit.checked()) {
                continue;
            }
            if (limit instanceof Each) {
                Limit each = ((Each) limit).limit();
                int i = 0;
                for (Object item : list) {
                    String atItem = here + "[" + i++ + "]";
                    String problem = each.problem(coerceElement(item, atItem));
                    if (problem != null) {
                        out.add(new Violation(atItem, problem, limit, null));
                    }
                }
            } else {
                String problem = limit.problem(list);
                if (problem != null) {
                    out.add(new Violation(here, problem, limit, null));
                }
            }
        }
        if (elementType == null) {
            int i = 0;
            for (Object item : list) {
                String atItem = here + "[" + i++ + "]";
                elementSchema().checkInto(asValues(item, atItem), atItem, out);
            }
        }
    }

    private int maxCount() {
        switch (countType) {
            case BYTE:
                return Byte.MAX_VALUE;
            case SHORT:
                return Short.MAX_VALUE;
            default:
                return Integer.MAX_VALUE;
        }
    }

    private Object coerceElement(Object item, String at) {
        try {
            return elementType.coerce(item);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(at + ": " + e.getMessage());
        }
    }
}
