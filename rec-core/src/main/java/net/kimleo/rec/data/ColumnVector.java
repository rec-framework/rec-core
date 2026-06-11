package net.kimleo.rec.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ColumnVector {

    private final DataType type;
    private final boolean[] nulls;
    private final Object data;

    private ColumnVector(DataType type, boolean[] nulls, Object data) {
        this.type = type;
        this.nulls = nulls;
        this.data = data;
    }

    public DataType type() { return type; }
    public boolean isNull(int index) { return nulls[index]; }
    public int size() { return nulls.length; }

    public String getString(int index) {
        if (nulls[index]) return null;
        switch (type) {
            case STRING: return ((String[]) data)[index];
            case INT: return String.valueOf(((int[]) data)[index]);
            case LONG: return String.valueOf(((long[]) data)[index]);
            case FLOAT: return String.valueOf(((float[]) data)[index]);
            case DOUBLE: return String.valueOf(((double[]) data)[index]);
            case BOOLEAN: return String.valueOf(((boolean[]) data)[index]);
            case DECIMAL: return ((BigDecimal[]) data)[index].toString();
            case DATE: return ((LocalDate[]) data)[index].toString();
            case TIMESTAMP: return ((LocalDateTime[]) data)[index].toString();
            default: return String.valueOf(get(index));
        }
    }

    public int getInt(int index) {
        if (nulls[index]) return 0;
        switch (type) {
            case INT: return ((int[]) data)[index];
            case LONG: return (int) ((long[]) data)[index];
            case FLOAT: return (int) ((float[]) data)[index];
            case DOUBLE: return (int) ((double[]) data)[index];
            case STRING: return Integer.parseInt(((String[]) data)[index]);
            default: throw wrongType("INT", type);
        }
    }

    public long getLong(int index) {
        if (nulls[index]) return 0L;
        switch (type) {
            case LONG: return ((long[]) data)[index];
            case INT: return ((int[]) data)[index];
            case FLOAT: return (long) ((float[]) data)[index];
            case DOUBLE: return (long) ((double[]) data)[index];
            case STRING: return Long.parseLong(((String[]) data)[index]);
            default: throw wrongType("LONG", type);
        }
    }

    public double getDouble(int index) {
        if (nulls[index]) return 0.0;
        switch (type) {
            case DOUBLE: return ((double[]) data)[index];
            case FLOAT: return ((float[]) data)[index];
            case INT: return ((int[]) data)[index];
            case LONG: return ((long[]) data)[index];
            case STRING: return Double.parseDouble(((String[]) data)[index]);
            default: throw wrongType("DOUBLE", type);
        }
    }

    public boolean getBoolean(int index) {
        if (nulls[index]) return false;
        switch (type) {
            case BOOLEAN: return ((boolean[]) data)[index];
            case STRING: return Boolean.parseBoolean(((String[]) data)[index]);
            case INT: return ((int[]) data)[index] != 0;
            default: throw wrongType("BOOLEAN", type);
        }
    }

    public byte[] getBytes(int index) {
        if (nulls[index]) return null;
        if (type == DataType.BYTES) return ((byte[][]) data)[index];
        throw wrongType("BYTES", type);
    }

    public BigDecimal getDecimal(int index) {
        if (nulls[index]) return null;
        if (type == DataType.DECIMAL) return ((BigDecimal[]) data)[index];
        if (type == DataType.STRING) return new BigDecimal(((String[]) data)[index]);
        if (type == DataType.DOUBLE) return BigDecimal.valueOf(((double[]) data)[index]);
        if (type == DataType.LONG) return BigDecimal.valueOf(((long[]) data)[index]);
        throw wrongType("DECIMAL", type);
    }

    public LocalDate getDate(int index) {
        if (nulls[index]) return null;
        if (type == DataType.DATE) return ((LocalDate[]) data)[index];
        if (type == DataType.STRING) return LocalDate.parse(((String[]) data)[index]);
        throw wrongType("DATE", type);
    }

    public LocalDateTime getTimestamp(int index) {
        if (nulls[index]) return null;
        if (type == DataType.TIMESTAMP) return ((LocalDateTime[]) data)[index];
        if (type == DataType.STRING) {
            return LocalDateTime.parse(((String[]) data)[index]);
        }
        throw wrongType("TIMESTAMP", type);
    }

    public Object get(int index) {
        if (nulls[index]) return null;
        switch (type) {
            case STRING: return ((String[]) data)[index];
            case INT: return ((int[]) data)[index];
            case LONG: return ((long[]) data)[index];
            case FLOAT: return ((float[]) data)[index];
            case DOUBLE: return ((double[]) data)[index];
            case BOOLEAN: return ((boolean[]) data)[index];
            case BYTES: return ((byte[][]) data)[index];
            case DECIMAL: return ((BigDecimal[]) data)[index];
            case DATE: return ((LocalDate[]) data)[index];
            case TIMESTAMP: return ((LocalDateTime[]) data)[index];
            default: throw wrongType("unknown", type);
        }
    }

    private static UnsupportedOperationException wrongType(
            String wanted, DataType actual) {
        return new UnsupportedOperationException(
                "Cannot get " + wanted + " from " + actual);
    }

    // ---- Builder ----

    public static Builder builder(DataType type, int capacity) {
        return new Builder(type, capacity);
    }

    public static class Builder {
        private final DataType type;
        private final boolean[] nulls;
        private final Object data;
        private int pos;

        private Builder(DataType type, int capacity) {
            this.type = type;
            this.nulls = new boolean[capacity];
            this.data = allocArray(type, capacity);
        }

        public Builder addNull() {
            nulls[pos++] = true;
            return this;
        }

        public Builder add(Object value) {
            if (value == null) {
                nulls[pos] = true;
            } else {
                nulls[pos] = false;
                setTyped(data, pos, type, value);
            }
            pos++;
            return this;
        }

        public ColumnVector build() {
            return new ColumnVector(type, nulls, data);
        }
    }

    // ---- Static helpers ----

    private static Object allocArray(DataType type, int size) {
        switch (type) {
            case STRING: return new String[size];
            case INT: return new int[size];
            case LONG: return new long[size];
            case FLOAT: return new float[size];
            case DOUBLE: return new double[size];
            case BOOLEAN: return new boolean[size];
            case BYTES: return new byte[size][];
            case DECIMAL: return new BigDecimal[size];
            case DATE: return new LocalDate[size];
            case TIMESTAMP: return new LocalDateTime[size];
            default: throw wrongType("array", type);
        }
    }

    private static void setTyped(
            Object arr, int pos, DataType type, Object val) {
        switch (type) {
            case STRING: ((String[]) arr)[pos] = val.toString(); break;
            case INT: ((int[]) arr)[pos] = toInt(val); break;
            case LONG: ((long[]) arr)[pos] = toLong(val); break;
            case FLOAT: ((float[]) arr)[pos] = toFloat(val); break;
            case DOUBLE: ((double[]) arr)[pos] = toDouble(val); break;
            case BOOLEAN: ((boolean[]) arr)[pos] = toBool(val); break;
            case BYTES: ((byte[][]) arr)[pos] = (byte[]) val; break;
            case DECIMAL: ((BigDecimal[]) arr)[pos] = toDecimal(val); break;
            case DATE: ((LocalDate[]) arr)[pos] = toDate(val); break;
            case TIMESTAMP: ((LocalDateTime[]) arr)[pos] = toTimestamp(val); break;
            default: throw wrongType("write", type);
        }
    }

    private static int toInt(Object v) {
        return (v instanceof Number)
                ? ((Number) v).intValue()
                : Integer.parseInt(v.toString());
    }

    private static long toLong(Object v) {
        return (v instanceof Number)
                ? ((Number) v).longValue()
                : Long.parseLong(v.toString());
    }

    private static float toFloat(Object v) {
        return (v instanceof Number)
                ? ((Number) v).floatValue()
                : Float.parseFloat(v.toString());
    }

    private static double toDouble(Object v) {
        return (v instanceof Number)
                ? ((Number) v).doubleValue()
                : Double.parseDouble(v.toString());
    }

    private static boolean toBool(Object v) {
        return (v instanceof Boolean)
                ? (Boolean) v
                : Boolean.parseBoolean(v.toString());
    }

    private static BigDecimal toDecimal(Object v) {
        return (v instanceof BigDecimal)
                ? (BigDecimal) v
                : new BigDecimal(v.toString());
    }

    private static LocalDate toDate(Object v) {
        return (v instanceof LocalDate)
                ? (LocalDate) v
                : LocalDate.parse(v.toString());
    }

    private static LocalDateTime toTimestamp(Object v) {
        return (v instanceof LocalDateTime)
                ? (LocalDateTime) v
                : LocalDateTime.parse(v.toString());
    }
}
