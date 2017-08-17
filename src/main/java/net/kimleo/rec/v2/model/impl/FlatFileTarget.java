package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.concept.Mapped;
import net.kimleo.rec.exception.ResourceAccessException;
import net.kimleo.rec.v2.model.Source;
import net.kimleo.rec.v2.model.Target;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class FlatFileTarget implements Target<Mapped<String>>, Closeable {

    private final PrintWriter writer;

    public FlatFileTarget(File file) {
        try {
            writer = new PrintWriter(Files.newBufferedWriter(file.toPath()));
        } catch (IOException e) {
            throw new ResourceAccessException(String.format("Cannot open output file: [%s]", file.getName()), e);
        }

    }

    @Override
    public void put(Mapped<String> record) {
        writer.println(record.keys().stream().map(record::get).collect(Collectors.joining(", ")));
    }

    @Override
    public void putAll(Source<Mapped<String>> source) {
        source.stream().forEach(this::put);
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.flush();
        writer.close();
    }
}
