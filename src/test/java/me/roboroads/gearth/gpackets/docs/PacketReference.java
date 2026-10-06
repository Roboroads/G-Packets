package me.roboroads.gearth.gpackets.docs;

import gearth.protocol.HMessage;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.PacketTypes;
import me.roboroads.gearth.gpackets.support.schema.BranchParameter;
import me.roboroads.gearth.gpackets.support.schema.ListParameter;
import me.roboroads.gearth.gpackets.support.schema.OptionalParameter;
import me.roboroads.gearth.gpackets.support.schema.Parameter;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.StructParameter;
import me.roboroads.gearth.gpackets.support.schema.ValueParameter;
import me.roboroads.gearth.gpackets.support.schema.WireType;
import me.roboroads.gearth.gpackets.support.schema.limit.Limit;
import me.roboroads.gearth.gpackets.support.schema.limit.Rule;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Writes the packet reference pages of the documentation site from the packet schemas: one Markdown
 * page per packet plus an index. The docs workflow runs it as
 * {@code java -cp ... me.roboroads.gearth.gpackets.docs.PacketReference docs/packets}.
 */
public final class PacketReference {

    private PacketReference() {
    }

    /** Writes the reference into the directory given as the only argument, for example {@code docs/packets}. */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: PacketReference <output directory>");
            System.exit(2);
        }
        write(PacketTypes.all(), Paths.get(args[0]));
    }

    static void write(List<PacketType<?>> types, Path directory) throws IOException {
        Files.createDirectories(directory);
        Files.write(directory.resolve("index.md"), index(types).getBytes(StandardCharsets.UTF_8));
        for (PacketType<?> type : types) {
            Path file = directory.resolve(path(type));
            Files.createDirectories(file.getParent());
            Files.write(file, page(type).getBytes(StandardCharsets.UTF_8));
        }
    }

    /** The page's path in the reference, per direction, because a header such as {@code Chat} exists both ways. */
    static String path(PacketType<?> type) {
        return (type.direction() == HMessage.Direction.TOCLIENT ? "incoming/" : "outgoing/") + type.header() + ".md";
    }

    static String index(List<PacketType<?>> types) {
        StringBuilder out = new StringBuilder("# Packet reference\n\n"
                + "Every packet G-Packets implements, generated from the packet schemas. "
                + "Each page lists the packet's parameters in the order they appear on the wire.\n");
        indexTable(out, "Incoming (to client)", types, HMessage.Direction.TOCLIENT);
        indexTable(out, "Outgoing (to server)", types, HMessage.Direction.TOSERVER);
        return out.toString();
    }

    private static void indexTable(StringBuilder out, String title, List<PacketType<?>> types, HMessage.Direction direction) {
        List<PacketType<?>> matching = new ArrayList<>();
        for (PacketType<?> type : types) {
            if (type.direction() == direction) {
                matching.add(type);
            }
        }
        matching.sort(Comparator.comparing((PacketType<?> type) -> type.header()));
        out.append("\n## ").append(title).append("\n\n| Header | Class |\n|---|---|\n");
        for (PacketType<?> type : matching) {
            out.append("| [").append(type.header()).append("](").append(path(type)).append(")")
                    .append(type.unused() == null ? "" : " (unused)")
                    .append(" | `").append(type.schema().type().getName()).append("` |\n");
        }
    }

    static String page(PacketType<?> type) {
        String name = type.schema().type().getSimpleName();
        String direction = type.direction() == HMessage.Direction.TOCLIENT ? "incoming (to client)" : "outgoing (to server)";
        return "# " + type.header() + "\n\n"
                + (type.unused() == null ? "" : "!!! warning \"Unused by the client\"\n    " + type.unused() + "\n\n")
                + "- Direction: " + direction + "\n"
                + "- Class: `" + type.schema().type().getName() + "`\n\n"
                + "```java\n@Intercept\nvoid on" + name + "(" + name + " packet) {\n    // ...\n}\n```\n\n"
                + (type.direction() == HMessage.Direction.TOSERVER && type.schema().hasChecks()
                    ? "Limits are checked when you send this packet, see [Limits](../../changing-and-sending.md#limits).\n\n" : "")
                + body(type.schema());
    }

    /** The "Parameters" section of a packet page, followed by one section per nested structure. */
    static String body(Schema<?> root) {
        return new Renderer(root).render();
    }

    private static final class Renderer {
        private final Schema<?> root;
        private final Set<Schema<?>> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        private final Deque<Schema<?>> pending = new ArrayDeque<>();

        Renderer(Schema<?> root) {
            this.root = root;
            seen.add(root);
        }

        String render() {
            StringBuilder out = new StringBuilder("## Parameters\n\n");
            out.append(section(root, 3));
            while (!pending.isEmpty()) {
                Schema<?> next = pending.removeFirst();
                out.append("\n## ").append(next.type().getSimpleName()).append("\n\n");
                out.append(section(next, 3));
            }
            return out.toString();
        }

        /** A table of the schema's parameters, then its enum options, then a subsection per branch case. */
        private String section(Schema<?> schema, int level) {
            List<String> rows = new ArrayList<>();
            List<ValueParameter> enums = new ArrayList<>();
            List<BranchParameter> branches = new ArrayList<>();
            Map<String, ValueParameter> values = new HashMap<>();
            collect(schema.parameters(), "", rows, enums, branches, values);

            StringBuilder out = new StringBuilder();
            if (rows.isEmpty()) {
                out.append("No parameters.\n");
            } else {
                out.append("| Name | Type | Notes |\n|---|---|---|\n");
                for (String row : rows) {
                    out.append(row).append('\n');
                }
            }
            if (!schema.rules().isEmpty()) {
                out.append("\nRules:\n\n");
                for (Rule rule : schema.rules()) {
                    out.append("- ").append(rule.description()).append('\n');
                }
            }
            for (ValueParameter value : enums) {
                out.append("\n`").append(value.name()).append("` (`").append(value.enumType().getSimpleName()).append("`): ")
                        .append(options(value)).append('\n');
            }
            for (BranchParameter branch : branches) {
                cases(branch, values.get(branch.on()), level, out);
            }
            return out.toString();
        }

        private void collect(List<Parameter> parameters, String note, List<String> rows, List<ValueParameter> enums,
                             List<BranchParameter> branches, Map<String, ValueParameter> values) {
            for (Parameter parameter : parameters) {
                if (parameter instanceof ValueParameter) {
                    ValueParameter value = (ValueParameter) parameter;
                    rows.add(row(value.name(), type(value), limitNote(unusedNote(note, value), value)));
                    values.put(value.name(), value);
                    if (value.enumType() != null) {
                        enums.add(value);
                    }
                } else if (parameter instanceof ListParameter) {
                    ListParameter list = (ListParameter) parameter;
                    String element = list.elementType() != null ? wireName(list.elementType()) : link(list.elementSchema());
                    String count = list.countType() == WireType.INT ? "" : ", counted by a " + wireName(list.countType());
                    rows.add(row(list.name(), "list of " + element + count, limitNote(unusedNote(note, list), list)));
                } else if (parameter instanceof StructParameter) {
                    rows.add(row(parameter.name(), link(((StructParameter) parameter).schema()), limitNote(unusedNote(note, parameter), parameter)));
                } else if (parameter instanceof OptionalParameter) {
                    // An optional inside an optional (each value read only if bytes are left) gets the note once.
                    String optional = "optional: only present if the packet has bytes left";
                    collect(((OptionalParameter) parameter).schema().parameters(),
                            note.contains(optional) ? note : join(note, optional), rows, enums, branches, values);
                } else if (parameter instanceof BranchParameter && ((BranchParameter) parameter).exhaustive()) {
                    BranchParameter branch = (BranchParameter) parameter;
                    rows.add(row(null, "", join(note, "depends on `" + subject(branch) + "`, see below")));
                    branches.add(branch);
                } else if (parameter instanceof BranchParameter) {
                    BranchParameter when = (BranchParameter) parameter;
                    // The values of a whenOneOf share one schema, so their parameters get one set of rows.
                    Map<Schema<?>, List<Object>> shared = new LinkedHashMap<>();
                    for (BranchParameter.Case c : when.cases().values()) {
                        shared.computeIfAbsent(c.schema(), schema -> new ArrayList<>()).add(c.value());
                    }
                    for (Map.Entry<Schema<?>, List<Object>> entry : shared.entrySet()) {
                        collect(entry.getKey().parameters(), join(note, "only when " + condition(when, entry.getValue())),
                                rows, enums, branches, values);
                    }
                }
            }
        }

        /** A subsection per case. Cases with the same subclass and the same parameters share one subsection. */
        private void cases(BranchParameter branch, ValueParameter discriminator, int level, StringBuilder out) {
            Map<String, CaseGroup> groups = new LinkedHashMap<>();
            for (BranchParameter.Case c : branch.cases().values()) {
                String subclass = c.subclass().getSimpleName();
                String section = section(c.schema(), level + 1);
                groups.computeIfAbsent(subclass + '\u0000' + section, key -> new CaseGroup(subclass, section))
                        .labels.add(label(c.value(), discriminator));
            }
            for (CaseGroup group : groups.values()) {
                out.append('\n').append(hashes(level)).append(" When `").append(subject(branch)).append("` is ")
                        .append(group.labels.size() == 1 ? group.labels.get(0) : "one of " + String.join(", ", group.labels))
                        .append(": `").append(group.subclass).append("`\n\n").append(group.section);
            }
        }

        /** When a conditional parameter is on the wire: the value it checks and the values that add it. */
        private static String condition(BranchParameter when, List<Object> values) {
            // A sign bit mask reads better as what it means.
            if (when.mask() != null && when.mask() == Integer.MIN_VALUE && values.size() == 1) {
                boolean negative = ((Number) values.get(0)).intValue() == Integer.MIN_VALUE;
                return "`" + when.on() + "` is " + (negative ? "negative" : "zero or more");
            }
            List<String> labels = new ArrayList<>();
            for (Object value : values) {
                labels.add("`" + literal(value) + "`");
            }
            return "`" + subject(when) + "` is " + (labels.size() == 1 ? labels.get(0) : "one of " + String.join(", ", labels));
        }

        /** What a branch looks at: the value's name, with the mask when only some bits pick the case. */
        private static String subject(BranchParameter branch) {
            return branch.mask() == null ? branch.on() : branch.on() + " & " + branch.mask();
        }

        /** A link to the schema's section, queueing the section if it has not been rendered yet. */
        private String link(Schema<?> schema) {
            if (seen.add(schema)) {
                pending.addLast(schema);
            }
            String name = schema.type().getSimpleName();
            return "[" + name + "](#" + (schema == root ? "parameters" : name.toLowerCase(Locale.ROOT)) + ")";
        }

        private static String row(String name, String type, String note) {
            return "| " + (name == null ? "" : "`" + name + "`") + " | " + type + " | " + note + " |";
        }

        private static String type(ValueParameter value) {
            String wire = wireName(value.wireType());
            return value.enumType() == null ? wire : wire + " → `" + value.enumType().getSimpleName() + "`";
        }

        private static String wireName(WireType wireType) {
            return wireType.name().toLowerCase(Locale.ROOT);
        }

        private static String options(ValueParameter value) {
            Map<String, String> unused = value.unusedOptions();
            List<String> options = new ArrayList<>();
            for (Map.Entry<String, Object> option : value.enumOptions().entrySet()) {
                options.add("`" + option.getKey() + "` = `" + literal(option.getValue()) + "`"
                        + (unused.containsKey(option.getKey()) ? " (unused)" : ""));
            }
            String listed = String.join(", ", options);
            return value.openEnum() ? listed + ". Other ids pass through." : listed;
        }

        /** The case's wire value, plus the enum constant's name when the discriminator is an enum. */
        private static String label(Object value, ValueParameter discriminator) {
            String label = "`" + literal(value) + "`";
            if (discriminator != null) {
                for (Map.Entry<String, Object> option : discriminator.enumOptions().entrySet()) {
                    if (option.getValue().equals(value)) {
                        return label + " (`" + option.getKey() + "`)";
                    }
                }
            }
            return label;
        }

        private static String literal(Object value) {
            return value instanceof String ? "\"" + value + "\"" : String.valueOf(value);
        }

        /** The note, plus why the client ignores the parameter when it does. */
        /** The note, plus the parameter's limits in words. */
        private static String limitNote(String note, Parameter parameter) {
            for (Limit limit : parameter.limits()) {
                note = join(note, limit.describe());
            }
            return note;
        }

        private static String unusedNote(String note, Parameter parameter) {
            return parameter.unused() == null ? note : join(note, "Unused by the client: " + parameter.unused());
        }

        private static String join(String note, String extra) {
            return note.isEmpty() ? extra : note + "; " + extra;
        }

        private static String hashes(int level) {
            StringBuilder hashes = new StringBuilder();
            for (int i = 0; i < level; i++) {
                hashes.append('#');
            }
            return hashes.toString();
        }
    }

    private static final class CaseGroup {
        final String subclass;
        final String section;
        final List<String> labels = new ArrayList<>();

        CaseGroup(String subclass, String section) {
            this.subclass = subclass;
            this.section = section;
        }
    }
}
