package net.kimleo.rec;

import net.kimleo.rec.accessor.AccessorFactory;
import net.kimleo.rec.record.parser.Configuration;
import net.kimleo.rec.record.Record;
import net.kimleo.rec.record.parser.SimpleParser;

import java.util.HashMap;
import java.util.Map;

public class API {
    private static Map<Configuration, SimpleParser> parsers = new HashMap<>();

    public static Record rec(String input) {
        return rec(input, new Configuration(',', '"'));
    }

    public static Record rec(String input, Configuration config) {
        if (!parsers.containsKey(config)) {
            parsers.put(config, new SimpleParser(config));
        }
        return parsers.get(config).parse(input);
    }

    public static AccessorFactory accessor(String ...fields) {
        return new AccessorFactory(fields);
    }

    public static AccessorFactory accessor(Record record) {
        return new AccessorFactory(record);
    }
}
