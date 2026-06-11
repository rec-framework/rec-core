package net.kimleo.rec.model.impl;

import net.kimleo.rec.common.exception.ResourceAccessException;
import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.data.DataUtils;
import net.kimleo.rec.data.Schema;
import net.kimleo.rec.model.Source;
import net.kimleo.rec.sepval.parser.ParseConfig;
import net.kimleo.rec.sepval.parser.SimpleParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CSVFileSource implements Source {

    private final Stream<String> lines;
    private final Schema schema;
    private final SimpleParser csvParser;
    private final int skipLimit;

    private CSVFileSource(Stream<String> lines, Schema schema,
                          SimpleParser csvParser, int skipLimit) {
        this.lines = lines;
        this.schema = schema;
        this.csvParser = csvParser;
        this.skipLimit = skipLimit;
    }

    public CSVFileSource(File file, String accessors, ParseConfig config) {
        List<String> names = new ArrayList<>(Arrays.asList(accessors.split(",")));
        for (int i = 0; i < names.size(); i++) {
            names.set(i, names.get(i).trim());
        }
        this.schema = Schema.of(names);
        this.csvParser = new SimpleParser(config);
        try {
            lines = Files.lines(file.toPath());
        } catch (IOException e) {
            throw new ResourceAccessException("CSV file cannot be found: [" + file.getName() + "]", e);
        }
        skipLimit = 0;
    }

    @Override
    public Stream<DataSet> stream() {
        return lines.skip(skipLimit).map(line -> {
            String[] values = csvParser.parse(line).getValues().toArray(new String[]{});
            return DataUtils.createRow(values, schema);
        });
    }

    @Override
    public Source skip(int n) {
        return new CSVFileSource(lines, schema, csvParser, n);
    }
}
