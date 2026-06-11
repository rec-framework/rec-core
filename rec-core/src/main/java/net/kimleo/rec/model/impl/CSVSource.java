package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.DataRow;
import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.data.DataUtils;
import net.kimleo.rec.data.DataType;
import net.kimleo.rec.data.Schema;
import net.kimleo.rec.model.Source;
import net.kimleo.rec.sepval.parser.ParseConfig;
import net.kimleo.rec.sepval.parser.SimpleParser;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CSVSource implements Source {

    private final BufferedReader reader;
    private final Schema schema;
    private final SimpleParser csvParser;
    private final int skipLimit;

    private CSVSource(BufferedReader reader, Schema schema,
                      SimpleParser csvParser, int skipLimit) {
        this.reader = reader;
        this.schema = schema;
        this.csvParser = csvParser;
        this.skipLimit = skipLimit;
    }

    public CSVSource(Reader reader, String accessors, ParseConfig config) {
        List<String> names = new ArrayList<>(Arrays.asList(accessors.split(",")));
        for (int i = 0; i < names.size(); i++) {
            names.set(i, names.get(i).trim());
        }
        this.schema = Schema.of(names);
        this.csvParser = new SimpleParser(config);
        this.reader = new BufferedReader(reader);
        this.skipLimit = 0;
    }

    @Override
    public Stream<DataSet> stream() {
        return reader.lines().skip(skipLimit).map(line -> {
            String[] values = csvParser.parse(line).getValues().toArray(new String[]{});
            return DataUtils.createRow(values, schema);
        });
    }

    @Override
    public Source skip(int n) {
        return new CSVSource(reader, schema, csvParser, n);
    }
}
