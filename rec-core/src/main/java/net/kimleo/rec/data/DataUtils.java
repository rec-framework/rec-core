package net.kimleo.rec.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

public final class DataUtils {

    private DataUtils() {}

    /**
     * Parse a CSV string value into the target DataType.
     * Returns null for empty/null input.
     */
    public static Object parseValue(String value, DataType type) {
        if (value == null || value.isEmpty()) return null;
        switch (type) {
            case STRING: return value;
            case INT: return Integer.parseInt(value.trim());
            case LONG: return Long.parseLong(value.trim());
            case FLOAT: return Float.parseFloat(value.trim());
            case DOUBLE: return Double.parseDouble(value.trim());
            case BOOLEAN:
                String v = value.trim().toLowerCase();
                return "true".equals(v) || "1".equals(v);
            case BYTES: return Base64.getDecoder().decode(value);
            case DECIMAL: return new BigDecimal(value.trim());
            case DATE: return LocalDate.parse(value.trim());
            case TIMESTAMP: return LocalDateTime.parse(value.trim());
            default: return value;
        }
    }

    /**
     * Infer DataType from a set of string values (sampling first N).
     */
    public static DataType inferType(String[] values, int sampleSize) {
        int max = Math.min(values.length, sampleSize);
        DataType inferred = DataType.STRING;
        for (int i = 0; i < max; i++) {
            if (values[i] == null || values[i].isEmpty()) continue;
            DataType t = DataType.infer(values[i]);
            if (t == DataType.STRING) return DataType.STRING;
            if (t.ordinal() > inferred.ordinal()) inferred = t;
        }
        return inferred;
    }

    /**
     * Create a DataRow from a string array using a Schema.
     * Strings are parsed according to column types.
     */
    public static DataRow createRow(String[] values, Schema schema) {
        int cols = schema.columnCount();
        ColumnVector[] vectors = new ColumnVector[cols];
        for (int i = 0; i < cols; i++) {
            Object parsed = i < values.length ? parseValue(values[i], schema.type(i)) : null;
            ColumnVector.Builder vb = ColumnVector.builder(schema.type(i), 1);
            vb.add(parsed);
            vectors[i] = vb.build();
        }
        return new DataRow(schema, vectors, 0);
    }
}
