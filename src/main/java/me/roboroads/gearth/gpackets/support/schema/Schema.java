package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * The wire format of a packet or sub-packet: its parameters in wire order, bound to the Java class
 * they describe. A schema reads a packet into named values and writes values back.
 *
 * <p>Schemas are immutable. Every DSL method returns a new schema with one more parameter.
 */
public final class Schema<T> {
    private final Class<T> type;
    private final List<Parameter> parameters;

    private Schema(Class<T> type, List<Parameter> parameters) {
        this.type = type;
        this.parameters = Collections.unmodifiableList(parameters);
    }

    /** An empty schema for {@code type}. */
    public static <T> Schema<T> of(Class<T> type) {
        return new Schema<>(Objects.requireNonNull(type, "type"), new ArrayList<>());
    }

    /** The class this schema describes. */
    public Class<T> type() {
        return type;
    }

    /** The parameters in wire order. */
    public List<Parameter> parameters() {
        return parameters;
    }

    public Schema<T> integer(String name) {
        return value(name, WireType.INT, null);
    }

    public Schema<T> string(String name) {
        return value(name, WireType.STRING, null);
    }

    public Schema<T> bool(String name) {
        return value(name, WireType.BOOLEAN, null);
    }

    public Schema<T> shortValue(String name) {
        return value(name, WireType.SHORT, null);
    }

    public Schema<T> longValue(String name) {
        return value(name, WireType.LONG, null);
    }

    public Schema<T> byteValue(String name) {
        return value(name, WireType.BYTE, null);
    }

    /** An int on the wire that maps to {@code enumType} through {@link IntEnum#value()}. */
    public <E extends Enum<E> & IntEnum> Schema<T> enumInt(String name, Class<E> enumType) {
        return value(name, WireType.INT, Objects.requireNonNull(enumType, "enumType"));
    }

    /** A string on the wire that maps to {@code enumType} through {@link StringEnum#code()}. */
    public <E extends Enum<E> & StringEnum> Schema<T> enumString(String name, Class<E> enumType) {
        return value(name, WireType.STRING, Objects.requireNonNull(enumType, "enumType"));
    }

    /** A list of primitives: an int count, then that many values. */
    public Schema<T> list(String name, WireType elementType) {
        return with(new ListParameter(Objects.requireNonNull(name, "name"), Objects.requireNonNull(elementType, "elementType"), null));
    }

    /** A list of structures: an int count, then that many {@code elementSchema}s. */
    public Schema<T> list(String name, Schema<?> elementSchema) {
        Objects.requireNonNull(elementSchema, "elementSchema");
        return list(name, () -> elementSchema);
    }

    /**
     * A list of structures whose schema is looked up when used, for a structure that contains
     * itself: {@code .list("children", () -> CatalogNode.SCHEMA)}. Use the qualified name; Java
     * rejects a simple-name self-reference in a field's own initializer.
     */
    public Schema<T> list(String name, Supplier<? extends Schema<?>> elementSchema) {
        return with(new ListParameter(Objects.requireNonNull(name, "name"), null, Objects.requireNonNull(elementSchema, "elementSchema")));
    }

    /** A nested structure, read inline. */
    public Schema<T> struct(String name, Schema<?> schema) {
        return with(new StructParameter(Objects.requireNonNull(name, "name"), Objects.requireNonNull(schema, "schema")));
    }

    /**
     * Parameters that depend on {@code on}, a value parameter added earlier to this schema. Each
     * case names a wire value (or enum constant), the subclass the object then is, and the
     * parameters that follow. A value without a case is an error.
     */
    public Schema<T> branch(String on, UnaryOperator<Cases> cases) {
        ValueParameter discriminator = discriminator(on);
        Cases built = Objects.requireNonNull(cases, "cases").apply(new Cases(discriminator, type));
        return with(new BranchParameter(discriminator, true, built.cases()));
    }

    /** Parameters that only follow when {@code on}, a value parameter added earlier, equals {@code value}. */
    public Schema<T> when(String on, Object value, UnaryOperator<Schema<T>> body) {
        ValueParameter discriminator = discriminator(on);
        Object key = discriminator.coerce(Objects.requireNonNull(value, "value"), type.getSimpleName() + "." + on);
        Map<Object, BranchParameter.Case> cases = new LinkedHashMap<>();
        cases.put(key, new BranchParameter.Case(key, null, Objects.requireNonNull(body, "body").apply(Schema.of(type))));
        return with(new BranchParameter(discriminator, false, cases));
    }

    /** Parameters read only if the packet has bytes left, and written only if any of their values is set. */
    public Schema<T> optional(UnaryOperator<Schema<T>> body) {
        return with(new OptionalParameter(Objects.requireNonNull(body, "body").apply(Schema.of(type))));
    }

    /** Reads values from the packet's current read index, keyed by parameter name in wire order. */
    public Map<String, Object> read(HPacket packet) {
        Map<String, Object> values = new LinkedHashMap<>();
        readInto(packet, values, type.getSimpleName());
        return values;
    }

    /** Appends the values to the packet. A missing or null value writes the wire default. */
    public void write(Map<String, Object> values, HPacket packet) {
        writeFrom(Objects.requireNonNull(values, "values"), packet, type.getSimpleName());
    }

    /** Reads a typed object from the packet's current read index. */
    public T parse(HPacket packet) {
        return Binder.bind(this, read(packet));
    }

    /** Appends a typed object to the packet. */
    public void append(T value, HPacket packet) {
        write(Binder.unbind(this, Objects.requireNonNull(value, "value")), packet);
    }

    void readInto(HPacket packet, Map<String, Object> values, String path) {
        for (Parameter parameter : parameters) {
            parameter.read(packet, values, path);
        }
    }

    void writeFrom(Map<String, Object> values, HPacket packet, String path) {
        for (Parameter parameter : parameters) {
            parameter.write(values, packet, path);
        }
    }

    private Schema<T> value(String name, WireType wireType, Class<? extends Enum<?>> enumType) {
        return with(new ValueParameter(Objects.requireNonNull(name, "name"), wireType, enumType));
    }

    private ValueParameter discriminator(String on) {
        for (Parameter parameter : parameters) {
            if (parameter instanceof ValueParameter && parameter.name().equals(on)) {
                return (ValueParameter) parameter;
            }
        }
        throw new IllegalArgumentException(type.getSimpleName() + " has no earlier value named " + on + " to branch on");
    }

    private Schema<T> with(Parameter parameter) {
        String name = parameter.name();
        if (name != null) {
            for (Parameter existing : parameters) {
                if (name.equals(existing.name())) {
                    throw new IllegalArgumentException(type.getSimpleName() + " already has a parameter named " + name);
                }
            }
        }
        List<Parameter> next = new ArrayList<>(parameters);
        next.add(parameter);
        return new Schema<>(type, next);
    }
}
