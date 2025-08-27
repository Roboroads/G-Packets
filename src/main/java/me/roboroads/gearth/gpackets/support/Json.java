package me.roboroads.gearth.gpackets.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Json {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.findAndRegisterModules();
    }

    private Json() {
    }

    public static String stringify(Object self) {
        try {
            return MAPPER.writeValueAsString(self);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize to JSON", e);
        }
    }

    public static <T> T parse(Class<T> self, String json) {
        try {
            return MAPPER.readValue(json, self);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON to " + self.getSimpleName(), e);
        }
    }
}

