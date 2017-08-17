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

public class CSVFileSource implements Source<Mapped<String>> {

    private final Stream<String> lines;
    private final Accessor<String> accessor;
    private final SimpleParser csvParser;
    private final int skipLimit;

    private CSVFileSource(Stream<String> lines,
                          Accessor<String> accessor,
                          SimpleParser csvParser,
                          int skipLimit) {
        this.lines = lines;
        this.accessor = accessor;
        this.csvParser = csvParser;
        this.skipLimit = skipLimit;
    }

    public CSVFileSource(File file, String accessors, ParseConfig config) {
        accessor = new Accessor<>(new SimpleParser().parse(accessors).getValues().toArray(new String[]{}));
        csvParser = new SimpleParser(config);
        try {
            lines = Files.lines(file.toPath());
        } catch (IOException e) {
            throw new ResourceAccessException("CSV file cannot be found: [" + file.getName() + "]", e);
        }
        skipLimit = 0;
    }

    @Override
    public Stream<Mapped<String>> stream() {
        return lines.skip(skipLimit).map(line -> {
            return accessor.of(csvParser.parse(line));
        });
    }

    @Override
    public Source<Mapped<String>> skip(int n) {
        return new CSVFileSource(lines, accessor, csvParser, n);
    }
}
