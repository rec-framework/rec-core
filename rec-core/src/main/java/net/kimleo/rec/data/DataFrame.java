package net.kimleo.rec.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Column-oriented data storage.
 * Data is stored as an array of ColumnVectors, one per column.
 */
public class DataFrame implements DataSet {

    private final Schema schema;
    private final ColumnVector[] columns;
    private final int rowCount;

    public DataFrame(Schema schema, ColumnVector[] columns) {
        this.schema = schema;
        this.columns = columns;
        this.rowCount = columns.length > 0 ? columns[0].size() : 0;
    }

    @Override
    public Schema schema() { return schema; }

    public int rowCount() { return rowCount; }

    public ColumnVector column(int index) { return columns[index]; }

    public ColumnVector column(String name) { return columns[schema.indexOf(name)]; }

    /**
     * Get a single-row view.
     */
    public DataRow row(int index) {
        return new DataRow(schema, columns, index);
    }

    /**
     * Stream all rows as DataSet instances.
     */
    public Stream<DataSet> rows() {
        return IntStream.range(0, rowCount).mapToObj(this::row);
    }

    // ---- DataSet interface (delegates to first row for single-row usage) ----

    @Override
    public boolean isNull(int col) { return columns[col].isNull(0); }

    @Override
    public boolean isNull(String field) { return isNull(schema.indexOf(field)); }

    @Override
    public String getString(int col) { return columns[col].getString(0); }

    @Override
    public int getInt(int col) { return columns[col].getInt(0); }

    @Override
    public long getLong(int col) { return columns[col].getLong(0); }

    @Override
    public float getFloat(int col) { return (float) columns[col].getDouble(0); }

    @Override
    public double getDouble(int col) { return columns[col].getDouble(0); }

    @Override
    public boolean getBoolean(int col) { return columns[col].getBoolean(0); }

    @Override
    public byte[] getBytes(int col) { return columns[col].getBytes(0); }

    @Override
    public BigDecimal getDecimal(int col) { return columns[col].getDecimal(0); }

    @Override
    public LocalDate getDate(int col) { return columns[col].getDate(0); }

    @Override
    public LocalDateTime getTimestamp(int col) { return columns[col].getTimestamp(0); }

    @Override
    public Object get(int col) { return columns[col].get(0); }

    // ---- Builder ----

    public static Builder builder(Schema schema) {
        return new Builder(schema);
    }

    public static class Builder {
        private final Schema schema;
        private final List<Object>[] rowBuffers;

        @SuppressWarnings("unchecked")
        public Builder(Schema schema) {
            this.schema = schema;
            this.rowBuffers = new List[schema.columnCount()];
            for (int i = 0; i < rowBuffers.length; i++) {
                rowBuffers[i] = new ArrayList<>();
            }
        }

        public Builder addRow(Object... values) {
            for (int i = 0; i < Math.min(values.length, rowBuffers.length); i++) {
                rowBuffers[i].add(values[i]);
            }
            return this;
        }

        public Builder addRow(DataSet row) {
            for (int i = 0; i < schema.columnCount(); i++) {
                rowBuffers[i].add(row.get(i));
            }
            return this;
        }

        public DataFrame build() {
            int size = rowBuffers.length > 0 ? rowBuffers[0].size() : 0;
            ColumnVector[] vectors = new ColumnVector[schema.columnCount()];
            for (int col = 0; col < schema.columnCount(); col++) {
                ColumnVector.Builder vb = ColumnVector.builder(schema.type(col), size);
                for (Object value : rowBuffers[col]) {
                    vb.add(value);
                }
                vectors[col] = vb.build();
            }
            return new DataFrame(schema, vectors);
        }
    }
}
