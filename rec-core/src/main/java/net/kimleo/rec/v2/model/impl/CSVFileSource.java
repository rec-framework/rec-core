package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.v2.accessor.Accessor;
import net.kimleo.rec.concept.Mapped;
import net.kimleo.rec.sepval.parser.ParseConfig;
import net.kimleo.rec.sepval.parser.SimpleParser;
import net.kimleo.rec.exception.ResourceAccessException;
import net.kimleo.rec.v2.model.Source;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

public class CSVFileSource implements Source {

    private final Stream<String> lines;
    private final Accessor<String> accessor;
    private final SimpleParser csvParser;

    public CSVFileSource(File file, String accessors, ParseConfig config) {
        accessor = new Accessor<>(new SimpleParser().parse(accessors).getValues().toArray(new String[]{}));
        csvParser = new SimpleParser(config);
        try {
            lines = Files.lines(file.toPath());
        } catch (IOException e) {
            throw new ResourceAccessException("CSV file cannot be found: [" + file.getName() + "]", e);
        }
    }

    @Override
    public Stream<Mapped<String>> stream() {
        return lines.map(line -> accessor.of(csvParser.parse(line)));
    }
}
