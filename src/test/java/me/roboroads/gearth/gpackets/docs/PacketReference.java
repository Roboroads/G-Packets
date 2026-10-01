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
            Files.write(directory.resolve(type.header() + ".md"), page(type).getBytes(StandardCharsets.UTF_8));
        }
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
            out.append("| [").append(type.header()).append("](").append(type.header()).append(".md) | `")
                    .append(type.schema().type().getName()).append("` |\n");
        }
    }

    static String page(PacketType<?> type) {
        String name = type.schema().type().getSimpleName();
        String direction = type.direction() == HMessage.Direction.TOCLIENT ? "incoming (to client)" : "outgoing (to server)";
        return "# " + type.header() + "\n\n"
                + "- Direction: " + direction + "\n"
                + "- Class: `" + type.schema().type().getName() + "`\n\n"
                + "```java\n@Intercept\nvoid on" + name + "(" + name + " packet) {\n    // ...\n}\n```\n\n"
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
                    rows.add(row(value.name(), type(value), note));
                    values.put(value.name(), value);
                    if (value.enumType() != null) {
                        enums.add(value);
                    }
                } else if (parameter instanceof ListParameter) {
                    ListParameter list = (ListParameter) parameter;
                    String element = list.elementType() != null ? wireName(list.elementType()) : link(list.elementSchema());
                    rows.add(row(list.name(), "list of " + element, note));
                } else if (parameter instanceof StructParameter) {
                    rows.add(row(parameter.name(), link(((StructParameter) parameter).schema()), note));
                } else if (parameter instanceof OptionalParameter) {
                    collect(((OptionalParameter) parameter).schema().parameters(),
                            join(note, "optional: only present if the packet has bytes left"), rows, enums, branches, values);
                } else if (parameter instanceof BranchParameter && ((BranchParameter) parameter).exhaustive()) {
                    BranchParameter branch = (BranchParameter) parameter;
                    rows.add(row(null, "", join(note, "depends on `" + branch.on() + "`, see below")));
                    branches.add(branch);
                } else if (parameter instanceof BranchParameter) {
                    BranchParameter when = (BranchParameter) parameter;
                    for (BranchParameter.Case c : when.cases().values()) {
                        collect(c.schema().parameters(), join(note, "only when `" + when.on() + "` is `" + literal(c.value()) + "`"),
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
                out.append('\n').append(hashes(level)).append(" When `").append(branch.on()).append("` is ")
                        .append(group.labels.size() == 1 ? group.labels.get(0) : "one of " + String.join(", ", group.labels))
                        .append(": `").append(group.subclass).append("`\n\n").append(group.section);
            }
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
            List<String> options = new ArrayList<>();
            for (Map.Entry<String, Object> option : value.enumOptions().entrySet()) {
                options.add("`" + option.getKey() + "` = `" + literal(option.getValue()) + "`");
            }
            return String.join(", ", options);
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
