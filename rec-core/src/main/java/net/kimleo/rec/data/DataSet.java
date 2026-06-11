package net.kimleo.rec.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The universal record type. Replaces Mapped&lt;String&gt;.
 * Supports both positional (column index) and named (field name) access.
 * All type conversions are safe: getString() always works.
 */
public interface DataSet {

    Schema schema();

    default int columnCount() { return schema().columnCount(); }

    default boolean hasField(String field) { return schema().hasField(field); }

    default List<String> fieldNames() { return schema().names(); }

    // ---- Null check ----
    boolean isNull(int col);
    boolean isNull(String field);

    // ---- Type-safe accessors (by index) ----
    String getString(int col);
    int getInt(int col);
    long getLong(int col);
    float getFloat(int col);
    double getDouble(int col);
    boolean getBoolean(int col);
    byte[] getBytes(int col);
    BigDecimal getDecimal(int col);
    LocalDate getDate(int col);
    LocalDateTime getTimestamp(int col);
    Object get(int col);

    // ---- Type-safe accessors (by name) ----
    default String getString(String field) { return getString(schema().indexOf(field)); }
    default int getInt(String field) { return getInt(schema().indexOf(field)); }
    default long getLong(String field) { return getLong(schema().indexOf(field)); }
    default float getFloat(String field) { return getFloat(schema().indexOf(field)); }
    default double getDouble(String field) { return getDouble(schema().indexOf(field)); }
    default boolean getBoolean(String field) { return getBoolean(schema().indexOf(field)); }
    default byte[] getBytes(String field) { return getBytes(schema().indexOf(field)); }
    default BigDecimal getDecimal(String field) { return getDecimal(schema().indexOf(field)); }
    default LocalDate getDate(String field) { return getDate(schema().indexOf(field)); }
    default LocalDateTime getTimestamp(String field) { return getTimestamp(schema().indexOf(field)); }
    default Object get(String field) { return get(schema().indexOf(field)); }
}
