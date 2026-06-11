package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.ColumnVector;
import net.kimleo.rec.data.DataRow;
import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.data.DataType;
import net.kimleo.rec.data.Schema;
import net.kimleo.rec.model.Source;
import org.apache.parquet.column.page.PageReadStore;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.io.ColumnIOFactory;
import org.apache.parquet.io.LocalInputFile;
import org.apache.parquet.io.MessageColumnIO;
import org.apache.parquet.io.RecordReader;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.io.api.PrimitiveConverter;
import org.apache.parquet.io.api.RecordMaterializer;
import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Reads Parquet files. No Hadoop dependency — uses LocalInputFile.
 */
public class ParquetSource implements Source {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ParquetSource.class);
    private final File file;

    public ParquetSource(File file) {
        this.file = file;
    }

    @Override
    public Stream<DataSet> stream() {
        try {
            LocalInputFile input = new LocalInputFile(file.toPath());
            ParquetFileReader reader = ParquetFileReader.open(input);
            MessageType schema =
                    reader.getFileMetaData().getSchema();
            Schema recSchema = toRecSchema(schema);
            MessageColumnIO colIO =
                    new ColumnIOFactory().getColumnIO(schema);
            return buildStream(reader, colIO, recSchema);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to read Parquet: " + file.getName(), e);
        }
    }

    private Stream<DataSet> buildStream(ParquetFileReader reader,
                                         MessageColumnIO colIO,
                                         Schema recSchema) {
        ParquetIter iter =
                new ParquetIter(reader, colIO, recSchema);
        Spliterator<DataSet> sp =
                Spliterators.spliteratorUnknownSize(
                        iter, Spliterator.ORDERED);
        return StreamSupport.stream(sp, false)
                .onClose(() -> closeReader(reader));
    }

    private void closeReader(ParquetFileReader r) {
        try { r.close(); } catch (IOException ignored) {}
    }

    private static class ParquetIter implements Iterator<DataSet> {
        private final ParquetFileReader reader;
        private final MessageColumnIO colIO;
        private final Schema recSchema;
        private PageReadStore pages;
        private RecordReader<DataSet> rr;
        private DataSet next;
        private boolean done;
        private long rowsRead;

        ParquetIter(ParquetFileReader reader,
                    MessageColumnIO colIO, Schema recSchema) {
            this.reader = reader;
            this.colIO = colIO;
            this.recSchema = recSchema;
        }

        @Override
        public boolean hasNext() {
            if (done) return false;
            if (next != null) return true;
            return advance();
        }

        @Override
        public DataSet next() {
            DataSet v = next;
            next = null;
            return v;
        }

        private boolean advance() {
            try {
                while (true) {
                    if (rr == null) {
                        pages = reader.readNextRowGroup();
                        if (pages == null) {
                            done = true;
                            return false;
                        }
                        MessageType fs = reader.getFileMetaData()
                                .getSchema();
                        rr = colIO.getRecordReader(pages,
                                new DataSetMat(fs, recSchema));
                        rowsRead = 0;
                    }
                    if (rowsRead >= pages.getRowCount()) {
                        rr = null;
                        continue;
                    }
                    DataSet row = rr.read();
                    rowsRead++;
                    if (row == null) { rr = null; continue; }
                    next = row;
                    return true;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Schema toRecSchema(MessageType msg) {
        List<String> names = new ArrayList<>();
        List<DataType> types = new ArrayList<>();
        for (Type f : msg.getFields()) {
            names.add(f.getName());
            types.add(toDataType(f));
        }
        return Schema.typed(names, types);
    }

    private DataType toDataType(Type field) {
        if (!field.isPrimitive()) return DataType.STRING;
        PrimitiveType pt = field.asPrimitiveType();
        LogicalTypeAnnotation lta = pt.getLogicalTypeAnnotation();
        switch (pt.getPrimitiveTypeName()) {
            case INT32: return int32(lta);
            case INT64: return int64(lta);
            case FLOAT: return DataType.FLOAT;
            case DOUBLE: return DataType.DOUBLE;
            case BOOLEAN: return DataType.BOOLEAN;
            case BINARY: return binary(lta);
            case FIXED_LEN_BYTE_ARRAY: return DataType.DECIMAL;
            case INT96: return DataType.TIMESTAMP;
            default: return DataType.STRING;
        }
    }

    private DataType int32(LogicalTypeAnnotation lta) {
        if (lta instanceof LogicalTypeAnnotation
                .DecimalLogicalTypeAnnotation) return DataType.DECIMAL;
        if (lta instanceof LogicalTypeAnnotation
                .DateLogicalTypeAnnotation) return DataType.DATE;
        return DataType.INT;
    }

    private DataType int64(LogicalTypeAnnotation lta) {
        if (lta instanceof LogicalTypeAnnotation
                .DecimalLogicalTypeAnnotation) return DataType.DECIMAL;
        return DataType.LONG;
    }

    private DataType binary(LogicalTypeAnnotation lta) {
        if (lta instanceof LogicalTypeAnnotation
                .DecimalLogicalTypeAnnotation) return DataType.DECIMAL;
        return DataType.STRING;
    }

    // ---- Record API ----

    private static class DataSetMat
            extends RecordMaterializer<DataSet> {
        private final GroupConv conv;
        DataSetMat(MessageType s, Schema r) {
            conv = new GroupConv(s, r);
        }
        @Override public DataSet getCurrentRecord() {
            return conv.build();
        }
        @Override public GroupConverter getRootConverter() {
            return conv;
        }
    }

    private static class GroupConv extends GroupConverter {
        private final Schema rec;
        private final int cols;
        private final Object[] vals;
        private final boolean[] nil;
        GroupConv(MessageType s, Schema r) {
            rec = r; cols = s.getFieldCount();
            vals = new Object[cols]; nil = new boolean[cols];
        }
        @Override public void start() {
            for (int i = 0; i < cols; i++) {
                vals[i] = null; nil[i] = true;
            }
        }
        @Override public void end() {}
        @Override public Converter getConverter(int i) {
            return new FC(i);
        }
        DataSet build() {
            ColumnVector[] v = new ColumnVector[cols];
            for (int i = 0; i < cols; i++) {
                ColumnVector.Builder b =
                        ColumnVector.builder(rec.type(i), 1);
                if (nil[i]) b.addNull(); else b.add(vals[i]);
                v[i] = b.build();
            }
            return new DataRow(rec, v, 0);
        }

        private class FC extends PrimitiveConverter {
            private final int i;
            FC(int i) { this.i = i; }
            @Override public void addBinary(Binary v) {
                DataType t = rec.type(i);
                if (t == DataType.BYTES) vals[i] = v.getBytes();
                else if (t == DataType.DECIMAL)
                    vals[i] = new BigDecimal(new BigInteger(v.getBytes()));
                else vals[i] = v.toStringUsingUTF8();
                nil[i] = false;
            }
            @Override public void addBoolean(boolean v) {
                vals[i] = v; nil[i] = false;
            }
            @Override public void addDouble(double v) {
                vals[i] = v; nil[i] = false;
            }
            @Override public void addFloat(float v) {
                vals[i] = (double) v; nil[i] = false;
            }
            @Override public void addInt(int v) {
                if (rec.type(i) == DataType.DATE)
                    vals[i] = LocalDate.ofEpochDay(v);
                else vals[i] = v;
                nil[i] = false;
            }
            @Override public void addLong(long v) {
                if (rec.type(i) == DataType.TIMESTAMP)
                    vals[i] = Instant.ofEpochSecond(
                            v / 1_000_000_000,
                            v % 1_000_000_000)
                            .atZone(ZoneOffset.UTC).toLocalDateTime();
                else vals[i] = v;
                nil[i] = false;
            }
        }
    }
}
