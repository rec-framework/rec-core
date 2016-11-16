package net.kimleo.rec;

import net.kimleo.rec.accessor.Accessor;
import net.kimleo.rec.record.*;
import net.kimleo.rec.sepval.parser.ParseConfig;
import net.kimleo.rec.sepval.parser.SimpleParser;
import net.kimleo.rec.repository.RecRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class API {
    private static final Map<ParseConfig, SimpleParser> parsers = new HashMap<>();

    public static Record rec(String input) {
        return rec(input, new ParseConfig(',', '"'));
    }

    public static Record rec(String input, ParseConfig config) {
        if (!parsers.containsKey(config)) {
            parsers.put(config, new SimpleParser(config));
        }
        return RecordKt.toRecord(parsers.get(config).parse(input));
    }

    public static Accessor accessor(String ...fields) {
        return new Accessor(fields);
    }

    public static Accessor accessor(Record record) {
        return new Accessor(record.getFields().stream().map(Field::getValue).collect(toList()).toArray(new String[record.getSize()]));
    }

    public static RecCollection collect(List<Record> records, RecType type) {
        return new RecCollection(records, type);
    }

    public static RecType type(final String name, final String format) {
        return DefaultRecType.Companion.create(name, format);
    }

    public static RecRepository repo(List<RecCollection> collects) {
        return new RecRepository(collects);
    }

}
