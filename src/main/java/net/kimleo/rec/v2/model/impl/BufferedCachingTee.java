package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.Pair;
import net.kimleo.rec.concept.Mapped;
import net.kimleo.rec.exception.ResourceAccessException;
import net.kimleo.rec.logging.Logger;
import net.kimleo.rec.logging.impl.LogManager;
import net.kimleo.rec.v2.model.Source;
import net.kimleo.rec.v2.model.Tee;
import net.kimleo.rec.v2.stream.adapter.GeneratingSpliteratorAdapter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static net.kimleo.rec.v2.utils.Records.decode;
import static net.kimleo.rec.v2.utils.Records.encode;

public class BufferedCachingTee implements Tee<Mapped<String>> {

    private static final Logger LOGGER = LogManager.logger(BufferedCachingTee.class.getName());

    private final Path tempFile;
    private final ByteBuffer buffer;

    private List<String> keys;
    private int writePos;

    public BufferedCachingTee(int size) {
        try {
            tempFile = Files.createTempFile(Paths.get("."),"rec-caching", ".bin");
            buffer = ByteBuffer.allocateDirect(size);
        } catch (IOException e) {
            throw new ResourceAccessException("Unable to create new temporary file.", e);
        }
    }

    @Override
    public Mapped<String> emit(Mapped<String> record) {
        if (keys == null) keys = record.keys();
        ByteBuffer bytes = encode(record, keys);
        bytes.position(0);
        buffer.position(writePos);
        buffer.put(bytes);

        writePos = buffer.position();
        return record;
    }

    @Override
    public Source<Mapped<String>> source() {
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

    static class BufferedCachingSource implements Source<Mapped<String>> {

        private final BufferedCachingTee tee;
        private final ByteBuffer buffer;
        private int readPos = 0;

        BufferedCachingSource(BufferedCachingTee tee) {
            this.tee = tee;
            this.buffer = tee.buffer;
        }

        @Override
        public Stream<Mapped<String>> stream() {
            return StreamSupport.stream(new GeneratingSpliteratorAdapter<Mapped<String>>(() -> {
                if (readPos >= tee.writePos) return null;
                Pair<List<String>, Integer> pair = decode(buffer, readPos, tee.keys.size());
                if (pair == null) return null;

                readPos += pair.second;
                return new Mapped<String>() {
                    @Override
                    public String get(String field) {
                        if (keys().contains(field))
                            return pair.getFirst().get(keys().indexOf(field));
                        return null;
                    }

                    @Override
                    public List<String> keys() {
                        return tee.keys;
                    }
                };
            }), false);
        }
    }
}
