package net.kimleo.rec.model.impl;

import net.kimleo.rec.common.Pair;
import net.kimleo.rec.common.exception.ResourceAccessException;
import net.kimleo.rec.data.DataRow;
import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.data.DataType;
import net.kimleo.rec.data.ColumnVector;
import net.kimleo.rec.data.Schema;
import net.kimleo.rec.model.Source;
import net.kimleo.rec.model.Tee;
import net.kimleo.rec.stream.adapter.GeneratingSpliteratorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static net.kimleo.rec.utils.Records.decode;
import static net.kimleo.rec.utils.Records.encode;

public class BufferedCachingTee implements Tee {

    private static final Logger LOGGER = LoggerFactory.getLogger(BufferedCachingTee.class);

    private final Path tempFile;
    private final ByteBuffer buffer;

    private Schema schema;
    private int writePos;

    public BufferedCachingTee(int size) {
        try {
            tempFile = Files.createTempFile(Paths.get("."), "rec-caching", ".bin");
            buffer = ByteBuffer.allocateDirect(size);
        } catch (IOException e) {
            throw new ResourceAccessException("Unable to create new temporary file.", e);
        }
    }

    @Override
    public DataSet emit(DataSet record) {
        if (schema == null) schema = record.schema();
        ByteBuffer bytes = encode(record, schema);
        bytes.position(0);
        buffer.position(writePos);
        buffer.put(bytes);
        writePos = buffer.position();
        return record;
    }

    @Override
    public Source source() {
        persist();
        return new BufferedCachingSource(this);
    }

    private void persist() {
        try {
            int original = buffer.limit();
            buffer.limit(writePos).position(0);
            byte[] bytes = new byte[writePos];
            buffer.get(bytes);
            Files.newByteChannel(tempFile,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING).write(ByteBuffer.wrap(bytes));
            buffer.limit(original);
        } catch (IOException e) {
            throw new ResourceAccessException(String.format("Cannot access temp file: [%s]",
                    tempFile.getFileName()), e);
        }
    }

    static class BufferedCachingSource implements Source {

        private final BufferedCachingTee tee;
        private final ByteBuffer buffer;
        private int readPos = 0;

        BufferedCachingSource(BufferedCachingTee tee) {
            this.tee = tee;
            this.buffer = tee.buffer;
        }

        @Override
        public Stream<DataSet> stream() {
            return StreamSupport.stream(new GeneratingSpliteratorAdapter<DataSet>(() -> {
                if (readPos >= tee.writePos) return null;
                Pair<List<String>, Integer> pair = decode(buffer, readPos, tee.schema.columnCount());
                if (pair == null) return null;
                readPos += pair.second;

                List<String> values = pair.getFirst();
                Schema schema = tee.schema;
                ColumnVector[] vectors = new ColumnVector[schema.columnCount()];
                for (int i = 0; i < schema.columnCount(); i++) {
                    String val = i < values.size() ? values.get(i) : null;
                    ColumnVector.Builder vb = ColumnVector.builder(schema.type(i), 1);
                    vb.add(val);
                    vectors[i] = vb.build();
                }
                return new DataRow(schema, vectors, 0);
            }), false);
        }
    }
}
