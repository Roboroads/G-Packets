package me.roboroads.gearth.gpackets.docs;

import me.roboroads.gearth.gpackets.support.schema.ListParameter;
import me.roboroads.gearth.gpackets.support.schema.OptionalParameter;
import me.roboroads.gearth.gpackets.support.schema.Parameter;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.StructParameter;
import me.roboroads.gearth.gpackets.support.schema.ValueParameter;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Writes the packet reference pages of the documentation site from the packet schemas: one Markdown
 * page per packet plus an index.
 */
public final class PacketReference {

    private PacketReference() {
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
            out.append(section(root));
            while (!pending.isEmpty()) {
                Schema<?> next = pending.removeFirst();
                out.append("\n## ").append(next.type().getSimpleName()).append("\n\n");
                out.append(section(next));
            }
            return out.toString();
        }

        /** A table of the schema's parameters, followed by the options of its enum parameters. */
        private String section(Schema<?> schema) {
            List<String> rows = new ArrayList<>();
            List<ValueParameter> enums = new ArrayList<>();
            collect(schema.parameters(), "", rows, enums);

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
            return out.toString();
        }

        private void collect(List<Parameter> parameters, String note, List<String> rows, List<ValueParameter> enums) {
            for (Parameter parameter : parameters) {
                if (parameter instanceof ValueParameter) {
                    ValueParameter value = (ValueParameter) parameter;
                    rows.add(row(value.name(), type(value), note));
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
                            join(note, "optional: only present if the packet has bytes left"), rows, enums);
                }
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

        private static String literal(Object value) {
            return value instanceof String ? "\"" + value + "\"" : String.valueOf(value);
        }

        private static String join(String note, String extra) {
            return note.isEmpty() ? extra : note + "; " + extra;
        }
    }
}
