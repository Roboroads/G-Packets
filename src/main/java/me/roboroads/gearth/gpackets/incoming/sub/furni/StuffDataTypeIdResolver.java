package me.roboroads.gearth.gpackets.incoming.sub.furni;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import me.roboroads.gearth.gpackets.support.schema.BranchParameter;
import me.roboroads.gearth.gpackets.support.schema.Parameter;

/**
 * Picks the {@link StuffData} subclass for JSON the way the schema does: from the format in the low
 * byte of {@code typeAndFlags}, so a limited edition or an unknown flag still finds its class.
 */
final class StuffDataTypeIdResolver extends TypeIdResolverBase {

    @Override
    public String idFromValue(Object value) {
        Integer typeAndFlags = ((StuffData) value).typeAndFlags();
        return typeAndFlags == null ? null : typeAndFlags.toString();
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return idFromValue(value);
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        int format;
        try {
            format = Integer.parseInt(id) & StuffData.FORMAT_MASK;
        } catch (NumberFormatException e) {
            return null;
        }
        BranchParameter.Case match = formats().cases().get(format);
        return match == null ? null : context.constructType(match.subclass());
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }

    private static BranchParameter formats() {
        for (Parameter parameter : StuffData.SCHEMA.parameters()) {
            if (parameter instanceof BranchParameter) {
                return (BranchParameter) parameter;
            }
        }
        throw new IllegalStateException("StuffData.SCHEMA has no branch");
    }
}
