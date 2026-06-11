package net.kimleo.rec.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public enum DataType {
    STRING(String.class),
    INT(int.class),
    LONG(long.class),
    FLOAT(float.class),
    DOUBLE(double.class),
    BOOLEAN(boolean.class),
    BYTES(byte[].class),
    DECIMAL(BigDecimal.class),
    DATE(LocalDate.class),
    TIMESTAMP(LocalDateTime.class);

    private final Class<?> javaType;

    DataType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public Class<?> javaType() {
        return javaType;
    }

    public Object defaultValue() {
        switch (this) {
            case STRING: return "";
            case INT: return 0;
            case LONG: return 0L;
            case FLOAT: return 0.0f;
            case DOUBLE: return 0.0;
            case BOOLEAN: return false;
            case BYTES: return new byte[0];
            case DECIMAL: return BigDecimal.ZERO;
            case DATE: return LocalDate.EPOCH;
            case TIMESTAMP: return LocalDateTime.of(1970, 1, 1, 0, 0);
            default: return null;
        }
    }

    public static DataType infer(String value) {
        if (value == null || value.isEmpty()) return STRING;
        // Try int
        try { Integer.parseInt(value); return INT; } catch (NumberFormatException ignored) {}
        // Try long
        try { Long.parseLong(value); return LONG; } catch (NumberFormatException ignored) {}
        // Try double
        try { Double.parseDouble(value); return DOUBLE; } catch (NumberFormatException ignored) {}
        // Try boolean
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) return BOOLEAN;
        return STRING;
    }
}
