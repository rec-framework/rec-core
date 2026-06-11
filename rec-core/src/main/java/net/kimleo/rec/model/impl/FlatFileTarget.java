package net.kimleo.rec.model.impl;

import net.kimleo.rec.common.exception.ResourceAccessException;
import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.model.Source;
import net.kimleo.rec.model.Target;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class FlatFileTarget implements Target, Closeable {

    private final PrintWriter writer;

    public FlatFileTarget(File file) {
        try {
            writer = new PrintWriter(Files.newBufferedWriter(file.toPath()));
        } catch (IOException e) {
            throw new ResourceAccessException(String.format("Cannot open output file: [%s]", file.getName()), e);
        }
    }

    @Override
    public void put(DataSet record) {
        writer.println(record.fieldNames().stream()
                .map(record::getString)
                .collect(Collectors.joining(", ")));
    }

    @Override
    public void putAll(Source source) {
        source.stream().forEach(this::put);
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.flush();
        writer.close();
    }
}
