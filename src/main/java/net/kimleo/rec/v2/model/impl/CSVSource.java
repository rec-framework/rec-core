package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.common.concept.Mapped;
import net.kimleo.rec.sepval.parser.ParseConfig;
import net.kimleo.rec.sepval.parser.SimpleParser;
import net.kimleo.rec.v2.accessor.Accessor;
import net.kimleo.rec.v2.model.Source;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.stream.Stream;

public class CSVSource implements Source<Mapped<String>> {

    private final BufferedReader reader;
    private final Accessor<String> accessor;
    private final SimpleParser csvParser;
    private final int skipLimit;

    private CSVSource(BufferedReader reader,
                      Accessor<String> accessor,
                      SimpleParser csvParser,
                      int skipLimit) {
        this.reader = reader;
        this.accessor = accessor;
        this.csvParser = csvParser;
        this.skipLimit = skipLimit;
    }

    public CSVSource(Reader reader, String accessors, ParseConfig config) {
        accessor = new Accessor<>(new SimpleParser().parse(accessors).getValues().toArray(new String[]{}));
        csvParser = new SimpleParser(config);
        this.reader = new BufferedReader(reader);
        skipLimit = 0;
    }

    @Override
    public Stream<Mapped<String>> stream() {
        return reader.lines().skip(skipLimit).map(line -> accessor.of(csvParser.parse(line)));
    }

    @Override
    public Source<Mapped<String>> skip(int n) {
        return new CSVSource(reader, accessor, csvParser, n);
    }
}
