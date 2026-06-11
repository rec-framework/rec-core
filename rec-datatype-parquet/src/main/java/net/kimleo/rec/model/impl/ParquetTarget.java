package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.data.DataType;
import net.kimleo.rec.data.Schema;
import net.kimleo.rec.model.Source;
import net.kimleo.rec.model.Target;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.io.LocalOutputFile;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.RecordConsumer;
import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Writes DataSet records as Parquet files.
 * No Hadoop dependency — uses LocalOutputFile.
 */
public class ParquetTarget implements Target, Closeable {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ParquetTarget.class);
    private final File file;
    private ParquetWriter<DataSet> writer;

    public ParquetTarget(File file) {
        this.file = file;
    }

    @Override
    public void put(DataSet record) {
        if (writer == null) initWriter(record.schema());
        try { writer.write(record); }
        catch (IOException e) {
            throw new RuntimeException("Write failed", e);
        }
    }

    @Override
    public void putAll(Source source) {
        source.stream().forEach(this::put);
        close();
    }

    @Override
    public void close() {
        if (writer != null) {
            try { writer.close(); }
            catch (IOException e) {
                throw new RuntimeException("Close failed", e);
            }
        }
    }

    private void initWriter(Schema schema) {
        MessageType ps = toParquetSchema(schema);
        if (file.exists()) file.delete();
        try {
            writer = new ParquetWriter<>(
                    new org.apache.hadoop.fs.Path(
                            file.getAbsolutePath()),
                    new DSWriteSupport(ps),
                    org.apache.parquet.hadoop.metadata
                            .CompressionCodecName.SNAPPY,
                    ParquetWriter.DEFAULT_BLOCK_SIZE,
                    ParquetWriter.DEFAULT_PAGE_SIZE);
        } catch (IOException e) {
            throw new RuntimeException("Init failed", e);
        }
    }

    // ---- Schema ----

    private MessageType toParquetSchema(Schema s) {
        Types.MessageTypeBuilder b = Types.buildMessage();
        for (int i = 0; i < s.columnCount(); i++) {
            b.addField(ptype(s.name(i), s.type(i)));
        }
        return b.named("rec_schema");
    }

    private org.apache.parquet.schema.Type ptype(
            String n, DataType t) {
        switch (t) {
            case INT: return opt32(n);
            case LONG: return opt64(n);
            case FLOAT: return optF(n);
            case DOUBLE: return optD(n);
            case BOOLEAN: return optB(n);
            case DATE: return optDate(n);
            case TIMESTAMP: return optTs(n);
            case DECIMAL: return optDec(n);
            case BYTES: return optBin(n);
            default: return optStr(n);
        }
    }

    private org.apache.parquet.schema.Type opt32(String n) {
        return Types.primitive(PrimitiveType.PrimitiveTypeName.INT32,
                org.apache.parquet.schema.Type.Repetition.OPTIONAL)
                .named(n);
    }

    private org.apache.parquet.schema.Type opt64(String n) {
        return Types.primitive(PrimitiveType.PrimitiveTypeName.INT64,
                org.apache.parquet.schema.Type.Repetition.OPTIONAL)
                .named(n);
    }

    private org.apache.parquet.schema.Type optF(String n) {
        return Types.primitive(PrimitiveType.PrimitiveTypeName.FLOAT,
                org.apache.parquet.schema.Type.Repetition.OPTIONAL)
                .named(n);
    }

    private org.apache.parquet.schema.Type optD(String n) {
        return Types.primitive(PrimitiveType.PrimitiveTypeName.DOUBLE,
                org.apache.parquet.schema.Type.Repetition.OPTIONAL)
                .named(n);
    }

    private org.apache.parquet.schema.Type optB(String n) {
        return Types.primitive(PrimitiveType.PrimitiveTypeName.BOOLEAN,
                org.apache.parquet.schema.Type.Repetition.OPTIONAL)
                .named(n);
    }

    private org.apache.parquet.schema.Type optDate(String n) {
        return Types.primitive(PrimitiveType.PrimitiveTypeName.INT32,
                org.apache.parquet.schema.Type.Repetition.OPTIONAL)
                .as(LogicalTypeAnnotation.dateType())
                .named(n);
    }

    private org.apache.parquet.schema.Type optTs(String n) {
        return Types.primitive(PrimitiveType.PrimitiveTypeName.INT64,
                org.apache.parquet.schema.Type.Repetition.OPTIONAL)
                .as(LogicalTypeAnnotation.timestampType(
                        false, LogicalTypeAnnotation.TimeUnit.MICROS))
                .named(n);
    }

    private org.apache.parquet.schema.Type optDec(String n) {
        return Types.primitive(PrimitiveType.PrimitiveTypeName.BINARY,
                org.apache.parquet.schema.Type.Repetition.OPTIONAL)
                .as(LogicalTypeAnnotation.decimalType(38, 18))
                .named(n);
    }

    private org.apache.parquet.schema.Type optBin(String n) {
        return Types.primitive(PrimitiveType.PrimitiveTypeName.BINARY,
                org.apache.parquet.schema.Type.Repetition.OPTIONAL)
                .named(n);
    }

    private org.apache.parquet.schema.Type optStr(String n) {
        return Types.primitive(PrimitiveType.PrimitiveTypeName.BINARY,
                org.apache.parquet.schema.Type.Repetition.OPTIONAL)
                .as(LogicalTypeAnnotation.stringType())
                .named(n);
    }

    // ---- WriteSupport ----

    private static class DSWriteSupport
            extends WriteSupport<DataSet> {
        private final MessageType schema;
        private RecordConsumer consumer;

        DSWriteSupport(MessageType schema) {
            this.schema = schema;
        }

        @Override
        public WriteContext init(
                org.apache.hadoop.conf.Configuration conf) {
            return new WriteContext(
                    schema, java.util.Collections.emptyMap());
        }

        @Override
        public void prepareForWrite(RecordConsumer c) {
            this.consumer = c;
        }

        @Override
        public void write(DataSet rec) {
            consumer.startMessage();
            for (int i = 0; i < schema.getFieldCount(); i++) {
                if (rec.isNull(i)) continue;
                org.apache.parquet.schema.Type f =
                        schema.getType(i);
                consumer.startField(f.getName(), i);
                writeField(rec, i, f);
                consumer.endField(f.getName(), i);
            }
            consumer.endMessage();
        }

        private void writeField(DataSet rec, int col,
                org.apache.parquet.schema.Type field) {
            PrimitiveType pt = field.asPrimitiveType();
            switch (pt.getPrimitiveTypeName()) {
                case INT32: writeI32(rec, col); break;
                case INT64: writeI64(rec, col); break;
                case FLOAT:
                    consumer.addFloat(rec.getFloat(col)); break;
                case DOUBLE:
                    consumer.addDouble(rec.getDouble(col)); break;
                case BOOLEAN:
                    consumer.addBoolean(rec.getBoolean(col)); break;
                case BINARY: writeBin(rec, col); break;
                case FIXED_LEN_BYTE_ARRAY:
                    writeDec(rec, col); break;
                default:
                    consumer.addBinary(
                            Binary.fromString(rec.getString(col)));
            }
        }

        private void writeI32(DataSet rec, int col) {
            if (rec.schema().type(col) == DataType.DATE) {
                LocalDate d = rec.getDate(col);
                consumer.addInteger((int) d.toEpochDay());
            } else {
                consumer.addInteger(rec.getInt(col));
            }
        }

        private void writeI64(DataSet rec, int col) {
            if (rec.schema().type(col) == DataType.TIMESTAMP) {
                LocalDateTime ts = rec.getTimestamp(col);
                long us = ts.toEpochSecond(ZoneOffset.UTC)
                        * 1_000_000 + ts.getNano() / 1000;
                consumer.addLong(us);
            } else {
                consumer.addLong(rec.getLong(col));
            }
        }

        private void writeBin(DataSet rec, int col) {
            if (rec.schema().type(col) == DataType.BYTES) {
                consumer.addBinary(
                        Binary.fromConstantByteArray(
                                rec.getBytes(col)));
            } else if (rec.schema().type(col) == DataType.DECIMAL) {
                BigDecimal d = rec.getDecimal(col);
                consumer.addBinary(
                        Binary.fromConstantByteArray(
                                d.unscaledValue().toByteArray()));
            } else {
                consumer.addBinary(
                        Binary.fromString(rec.getString(col)));
            }
        }

        private void writeDec(DataSet rec, int col) {
            BigDecimal d = rec.getDecimal(col);
            consumer.addBinary(
                    Binary.fromConstantByteArray(
                            d.unscaledValue().toByteArray()));
        }
    }
}
