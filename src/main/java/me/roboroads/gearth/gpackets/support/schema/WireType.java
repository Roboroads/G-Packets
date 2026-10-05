package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HPacket;

/**
 * The primitive types a packet is made of, with how each is read, written and defaulted.
 */
public enum WireType {
    INT, STRING, BOOLEAN, SHORT, LONG, BYTE, FLOAT, DOUBLE;

    /** The value written for null: {@code 0}, {@code ""} or {@code false}. */
    public Object defaultValue() {
        switch (this) {
            case INT:
                return 0;
            case STRING:
                return "";
            case BOOLEAN:
                return false;
            case SHORT:
                return (short) 0;
            case LONG:
                return 0L;
            case BYTE:
                return (byte) 0;
            case FLOAT:
                return 0f;
            case DOUBLE:
                return 0d;
            default:
                throw new AssertionError(this);
        }
    }

    Object read(HPacket packet) {
        switch (this) {
            case INT:
                return packet.readInteger();
            case STRING:
                return packet.readString();
            case BOOLEAN:
                return packet.readBoolean();
            case SHORT:
                return packet.readShort();
            case LONG:
                return packet.readLong();
            case BYTE:
                return packet.readByte();
            case FLOAT:
                return packet.readFloat();
            case DOUBLE:
                return packet.readDouble();
            default:
                throw new AssertionError(this);
        }
    }

    /** Writes a value that {@link #coerce} already accepted. */
    void write(HPacket packet, Object value) {
        switch (this) {
            case INT:
                packet.appendInt((Integer) value);
                break;
            case STRING:
                packet.appendString((String) value);
                break;
            case BOOLEAN:
                packet.appendBoolean((Boolean) value);
                break;
            case SHORT:
                packet.appendShort((Short) value);
                break;
            case LONG:
                packet.appendLong((Long) value);
                break;
            case BYTE:
                packet.appendByte((Byte) value);
                break;
            case FLOAT:
                packet.appendFloat((Float) value);
                break;
            case DOUBLE:
                packet.appendDouble((Double) value);
                break;
            default:
                throw new AssertionError(this);
        }
    }

    /**
     * Converts a value to this type's Java type. Null becomes the default, and any number is
     * narrowed for the numeric types.
     *
     * @throws IllegalArgumentException if the value cannot be this type.
     */
    Object coerce(Object value) {
        if (value == null) {
            return defaultValue();
        }
        if (this == STRING && value instanceof String) {
            return value;
        }
        if (this == BOOLEAN && value instanceof Boolean) {
            return value;
        }
        if (value instanceof Number) {
            Number number = (Number) value;
            switch (this) {
                case INT:
                    return number.intValue();
                case SHORT:
                    return number.shortValue();
                case LONG:
                    return number.longValue();
                case BYTE:
                    return number.byteValue();
                case FLOAT:
                    return number.floatValue();
                case DOUBLE:
                    return number.doubleValue();
                default:
                    break;
            }
        }
        throw new IllegalArgumentException("expected " + this + ", got " + value.getClass().getName());
    }
}
