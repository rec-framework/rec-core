package net.kimleo.rec.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A single-row view into a DataFrame or a standalone row.
 * This is the per-record type flowing through Source → Tee → Target.
 */
public class DataRow implements DataSet {

    private final Schema schema;
    private final ColumnVector[] columns;
    private final int rowIndex;

    public DataRow(Schema schema, ColumnVector[] columns, int rowIndex) {
        this.schema = schema;
        this.columns = columns;
        this.rowIndex = rowIndex;
    }

    @Override
    public Schema schema() { return schema; }

    @Override
    public boolean isNull(int col) { return columns[col].isNull(rowIndex); }

    @Override
    public boolean isNull(String field) { return isNull(schema.indexOf(field)); }

    @Override
    public String getString(int col) { return columns[col].getString(rowIndex); }

    @Override
    public int getInt(int col) { return columns[col].getInt(rowIndex); }

    @Override
    public long getLong(int col) { return columns[col].getLong(rowIndex); }

    @Override
    public float getFloat(int col) { return (float) columns[col].getDouble(rowIndex); }

    @Override
    public double getDouble(int col) { return columns[col].getDouble(rowIndex); }

    @Override
    public boolean getBoolean(int col) { return columns[col].getBoolean(rowIndex); }

    @Override
    public byte[] getBytes(int col) { return columns[col].getBytes(rowIndex); }

    @Override
    public BigDecimal getDecimal(int col) { return columns[col].getDecimal(rowIndex); }

    @Override
    public LocalDate getDate(int col) { return columns[col].getDate(rowIndex); }

    @Override
    public LocalDateTime getTimestamp(int col) { return columns[col].getTimestamp(rowIndex); }

    @Override
    public Object get(int col) { return columns[col].get(rowIndex); }
}
