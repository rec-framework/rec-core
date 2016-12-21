package net.kimleo.rec;

import net.kimleo.rec.accessor.Accessor;
import net.kimleo.rec.repository.DefaultRecConfig;
import net.kimleo.rec.repository.RecordSet;
import net.kimleo.rec.repository.RecConfig;
import net.kimleo.rec.record.*;
import net.kimleo.rec.sepval.parser.ParseConfig;
import net.kimleo.rec.sepval.parser.SimpleParser;
import net.kimleo.rec.repository.RecRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static net.kimleo.rec.record.Record.toRecord;

public class API {
    private static final Map<ParseConfig, SimpleParser> parsers = new HashMap<>();

    public static Record rec(String input) {
        return rec(input, new ParseConfig(',', '"'));
    }

    public static Record rec(String input, ParseConfig config) {
        if (!parsers.containsKey(config)) {
            parsers.put(config, new SimpleParser(config));
        }
        return toRecord(parsers.get(config).parse(input));
    }

    public static Accessor<String> accessor(String ...fields) {
        return new Accessor<>(fields);
    }

    public static Accessor<String> accessor(Record record) {
        return new Accessor<>(record.getCells().stream().map(Cell::getValue).collect(toList()).toArray(new String[record.getSize()]));
    }

    public static RecordSet collect(List<Record> records, RecConfig type) {
        return new RecordSet(records, type);
    }

    public static RecConfig type(final String name, final String format) {
        return DefaultRecConfig.create(name, format);
    }

    public static RecRepository repo(List<RecordSet> collects) {
        return new RecRepository(collects);
    }

}
