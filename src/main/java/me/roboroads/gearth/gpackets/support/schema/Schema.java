package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.limit.Each;
import me.roboroads.gearth.gpackets.support.schema.limit.Limit;
import me.roboroads.gearth.gpackets.support.schema.limit.Rule;
import me.roboroads.gearth.gpackets.support.schema.limit.Violation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
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
    private final List<Rule> rules;
    // Computed on first use: a list's element schema may be a supplier for a schema built later.
    private volatile Boolean hasChecks;

    private Schema(Class<T> type, List<Parameter> parameters, List<Rule> rules) {
        this.type = type;
        this.parameters = Collections.unmodifiableList(parameters);
        this.rules = Collections.unmodifiableList(rules);
    }

    /** An empty schema for {@code type}. */
    public static <T> Schema<T> of(Class<T> type) {
        return new Schema<>(Objects.requireNonNull(type, "type"), new ArrayList<>(), new ArrayList<>());
    }

    /** The class this schema describes. */
    public Class<T> type() {
        return type;
    }

    /** The parameters in wire order. */
    public List<Parameter> parameters() {
        return parameters;
    }

    public Schema<T> integer(String name, Limit... limits) {
        return value(name, WireType.INT, null, limits);
    }

    public Schema<T> string(String name, Limit... limits) {
        return value(name, WireType.STRING, null, limits);
    }

    public Schema<T> bool(String name, Limit... limits) {
        return value(name, WireType.BOOLEAN, null, limits);
    }

    public Schema<T> shortValue(String name, Limit... limits) {
        return value(name, WireType.SHORT, null, limits);
    }

    public Schema<T> longValue(String name, Limit... limits) {
        return value(name, WireType.LONG, null, limits);
    }

    public Schema<T> byteValue(String name, Limit... limits) {
        return value(name, WireType.BYTE, null, limits);
    }

    /** An int on the wire that maps to {@code enumType} through {@link IntEnum#value()}. */
    public <E extends Enum<E> & IntEnum> Schema<T> enumInt(String name, Class<E> enumType, Limit... limits) {
        return value(name, WireType.INT, Objects.requireNonNull(enumType, "enumType"), limits);
    }

    /** A string on the wire that maps to {@code enumType} through {@link StringEnum#code()}. */
    public <E extends Enum<E> & StringEnum> Schema<T> enumString(String name, Class<E> enumType, Limit... limits) {
        return value(name, WireType.STRING, Objects.requireNonNull(enumType, "enumType"), limits);
    }

    /** A list of primitives: an int count, then that many values. */
    public Schema<T> list(String name, WireType elementType, Limit... limits) {
        return with(new ListParameter(Objects.requireNonNull(name, "name"), Objects.requireNonNull(elementType, "elementType"), null), limits);
    }

    /** A list of structures: an int count, then that many {@code elementSchema}s. */
    public Schema<T> list(String name, Schema<?> elementSchema, Limit... limits) {
        Objects.requireNonNull(elementSchema, "elementSchema");
        return list(name, () -> elementSchema, limits);
    }

    /**
     * A list of structures whose schema is looked up when used, for a structure that contains
     * itself: {@code .list("children", () -> CatalogNode.SCHEMA)}. Use the qualified name; Java
     * rejects a simple-name self-reference in a field's own initializer.
     */
    public Schema<T> list(String name, Supplier<? extends Schema<?>> elementSchema, Limit... limits) {
        return with(new ListParameter(Objects.requireNonNull(name, "name"), null, Objects.requireNonNull(elementSchema, "elementSchema")), limits);
    }

    /** A nested structure, read inline. */
    public Schema<T> struct(String name, Schema<?> schema, Limit... limits) {
        return with(new StructParameter(Objects.requireNonNull(name, "name"), Objects.requireNonNull(schema, "schema")), limits);
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

    /**
     * A limit across several parameters of this schema: {@code check} gets the values as they
     * would be written (a missing value as its wire default, an enum constant as its wire value)
     * and returns true when they are fine.
     */
    public Schema<T> rule(String description, Predicate<Map<String, Object>> check) {
        List<Rule> next = new ArrayList<>(rules);
        next.add(new Rule(description, check));
        return new Schema<>(type, parameters, next);
    }

    /** The rules declared with {@link #rule}. */
    public List<Rule> rules() {
        return rules;
    }

    /** The limits and rules these values break, as they would be written. Empty when everything fits. */
    public List<Violation> violations(Map<String, Object> values) {
        List<Violation> out = new ArrayList<>();
        checkInto(Objects.requireNonNull(values, "values"), "", out);
        return out;
    }

    /** The limits and rules this object breaks. Empty when everything fits. */
    public List<Violation> violations(T value) {
        return violations(Binder.unbind(this, Objects.requireNonNull(value, "value")));
    }

    /** Whether this schema or any schema inside it has a checked limit or a rule. */
    public boolean hasChecks() {
        Boolean cached = hasChecks;
        if (cached == null) {
            cached = hasChecks(this, Collections.newSetFromMap(new IdentityHashMap<>()));
            hasChecks = cached;
        }
        return cached;
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

    void checkInto(Map<String, Object> values, String path, List<Violation> out) {
        for (Parameter parameter : parameters) {
            parameter.check(values, path, out);
        }
        if (!rules.isEmpty()) {
            Map<String, Object> written = new LinkedHashMap<>(values);
            putWritten(this, values, written, path);
            for (Rule rule : rules) {
                if (!rule.holds(written)) {
                    out.add(new Violation(path.isEmpty() ? type.getSimpleName() : path, rule.description(), null, rule));
                }
            }
        }
    }

    /** Puts every value parameter's value as it would be written, including those of optional bodies and the matching branch case. */
    private static void putWritten(Schema<?> schema, Map<String, Object> values, Map<String, Object> written, String path) {
        for (Parameter parameter : schema.parameters) {
            if (parameter instanceof ValueParameter) {
                written.put(parameter.name(), ((ValueParameter) parameter).coerce(values.get(parameter.name()), Parameter.at(path, parameter.name())));
            } else if (parameter instanceof OptionalParameter) {
                putWritten(((OptionalParameter) parameter).schema(), values, written, path);
            } else if (parameter instanceof BranchParameter) {
                BranchParameter branch = (BranchParameter) parameter;
                BranchParameter.Case match = branch.caseFor(values.get(branch.on()), path);
                if (match != null) {
                    putWritten(match.schema(), values, written, path);
                }
            }
        }
    }

    private static boolean hasChecks(Schema<?> schema, Set<Schema<?>> seen) {
        if (!seen.add(schema)) {
            return false;
        }
        if (!schema.rules.isEmpty()) {
            return true;
        }
        for (Parameter parameter : schema.parameters) {
            for (Limit limit : parameter.limits()) {
                if (limit.checked()) {
                    return true;
                }
            }
            if (parameter instanceof StructParameter && hasChecks(((StructParameter) parameter).schema(), seen)) {
                return true;
            }
            if (parameter instanceof ListParameter) {
                Schema<?> element = ((ListParameter) parameter).elementSchema();
                if (element != null && hasChecks(element, seen)) {
                    return true;
                }
            }
            if (parameter instanceof OptionalParameter && hasChecks(((OptionalParameter) parameter).schema(), seen)) {
                return true;
            }
            if (parameter instanceof BranchParameter) {
                for (BranchParameter.Case c : ((BranchParameter) parameter).cases().values()) {
                    if (hasChecks(c.schema(), seen)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Schema<T> value(String name, WireType wireType, Class<? extends Enum<?>> enumType, Limit... limits) {
        return with(new ValueParameter(Objects.requireNonNull(name, "name"), wireType, enumType), limits);
    }

    private ValueParameter discriminator(String on) {
        for (Parameter parameter : parameters) {
            if (parameter instanceof ValueParameter && parameter.name().equals(on)) {
                return (ValueParameter) parameter;
            }
        }
        throw new IllegalArgumentException(type.getSimpleName() + " has no earlier value named " + on + " to branch on");
    }

    private Schema<T> with(Parameter parameter, Limit... limits) {
        for (Limit limit : limits) {
            requireFits(parameter, Objects.requireNonNull(limit, "limit"));
        }
        // Branch and optional values share the enclosing map, so their names must not clash with it.
        Set<String> taken = valueNames(parameters);
        for (String name : valueNames(Collections.singletonList(parameter))) {
            if (taken.contains(name)) {
                throw new IllegalArgumentException(type.getSimpleName() + " already has a parameter named " + name);
            }
        }
        parameter.markUnused(unusedReason(type, parameter.name()));
        parameter.limits(Arrays.asList(limits));
        List<Parameter> next = new ArrayList<>(parameters);
        next.add(parameter);
        return new Schema<>(type, next, rules);
    }

    private void requireFits(Parameter parameter, Limit limit) {
        boolean fits;
        switch (limit.target()) {
            case STRING:
                fits = isPlainValue(parameter) && ((ValueParameter) parameter).wireType() == WireType.STRING;
                break;
            case NUMBER:
                fits = isPlainValue(parameter) && isNumber(((ValueParameter) parameter).wireType());
                break;
            case VALUE:
                fits = parameter instanceof ValueParameter;
                break;
            case LIST:
                fits = parameter instanceof ListParameter;
                break;
            case EACH:
                fits = parameter instanceof ListParameter && fitsElement(((Each) limit).limit(), ((ListParameter) parameter).elementType());
                break;
            default:
                fits = true;
        }
        if (!fits) {
            throw new IllegalArgumentException(type.getSimpleName() + "." + parameter.name() + ": " + limit.describe()
                    + " doesn't apply to this parameter");
        }
    }

    private static boolean isPlainValue(Parameter parameter) {
        return parameter instanceof ValueParameter && ((ValueParameter) parameter).enumType() == null;
    }

    private static boolean isNumber(WireType wireType) {
        return wireType == WireType.INT || wireType == WireType.SHORT || wireType == WireType.LONG || wireType == WireType.BYTE;
    }

    private static boolean fitsElement(Limit limit, WireType elementType) {
        if (elementType == null) {
            return false;
        }
        switch (limit.target()) {
            case STRING:
                return elementType == WireType.STRING;
            case NUMBER:
                return isNumber(elementType);
            default:
                return true;
        }
    }

    /**
     * The reason on the {@link Unused} field called {@code name}, declared on {@code type} or the
     * nearest superclass that declares it, or null when that field is unmarked or doesn't exist.
     */
    private static String unusedReason(Class<?> type, String name) {
        if (name == null) {
            return null;
        }
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                Unused unused = c.getDeclaredField(name).getAnnotation(Unused.class);
                return unused == null ? null : unused.value();
            } catch (NoSuchFieldException e) {
                // Not declared here; look in the superclass.
            }
        }
        return null;
    }

    /** The keys these parameters put in the enclosing values, including those of branch cases and optionals. */
    private static Set<String> valueNames(List<Parameter> parameters) {
        Set<String> names = new HashSet<>();
        for (Parameter parameter : parameters) {
            if (parameter instanceof BranchParameter) {
                // Cases exclude each other, so names shared between cases of one branch are fine.
                for (BranchParameter.Case c : ((BranchParameter) parameter).cases().values()) {
                    names.addAll(valueNames(c.schema().parameters()));
                }
            } else if (parameter instanceof OptionalParameter) {
                names.addAll(valueNames(((OptionalParameter) parameter).schema().parameters()));
            } else {
                names.add(parameter.name());
            }
        }
        return names;
    }
}
