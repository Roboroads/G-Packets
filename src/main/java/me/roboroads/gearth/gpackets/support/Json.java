package me.roboroads.gearth.gpackets.support;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public final class Json {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.findAndRegisterModules();
        // Lombok's fluent accessors (text() instead of getText()) are invisible to Jackson,
        // so serialize from the fields instead. Setters stay visible: @Jacksonized builders
        // deserialize through them.
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        MAPPER.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        // Packets without a body have no fields and serialize to {}.
        MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
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

