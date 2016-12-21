package net.kimleo.rec.repository;

import net.kimleo.rec.accessor.Accessor;
import net.kimleo.rec.accessor.RecordWrapper;
import net.kimleo.rec.collection.LinkedMultiHashMap;
import net.kimleo.rec.collection.MultiMap;
import net.kimleo.rec.record.Cell;
import net.kimleo.rec.record.Record;
import net.kimleo.rec.sepval.parser.ParseConfig;
import net.kimleo.rec.sepval.parser.SimpleParser;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;

public class RecordSet implements Iterable<Record> {
    private final List<Record> records;
    private final RecConfig config;
    private final Map<String, MultiMap<String, Record>> indices;
    private final Accessor<String> accessor;

    public List<Record> getRecords() {
        return records;
    }

    public RecConfig getConfig() {
        return config;
    }

    public Accessor<String> getAccessor() {
        return accessor;
    }

    public RecordSet(List<Record> records, RecConfig config) {
        this.records = records;
        this.config = config;
        this.accessor = config.accessor();
        this.indices = buildIndices(records, config.accessor());
    }

    private Map<String, MultiMap<String, Record>> buildIndices(List<Record> records, Accessor<String> accessor) {
        HashMap<String, MultiMap<String, Record>> indices = new HashMap<>();

        for (String key : accessor.getFieldMap().keySet()) {
            LinkedMultiHashMap<String, Record> index = new LinkedMultiHashMap<>();
            for (Record record : records) {
                index.put1(accessor.of(record).get(key), record);
            }
            indices.put(key, index);
        }
        return indices;
    }

    @Override
    public Iterator<Record> iterator() {
        return records.iterator();
    }

    public RecordSet select(List<String> keys, String name) {
        checkKeyExists(keys);

        RecConfig newType = newType(keys, name);
        ArrayList<Record> newRecords = new ArrayList<>();
        for (Record record : this.records) {
            ArrayList<String> fields = new ArrayList<>();
            RecordWrapper<String> acc = accessor.of(record);
            for (String key : keys) {
                fields.add(acc.get(key));
            }
            newRecords.add(new Record(fields.stream().map(Cell::new).collect(Collectors.toList()), record.parent()));
        }

        return new RecordSet(newRecords, newType);
    }

    public RecordSet select(String... keys) {
        return select(Arrays.asList(keys), null);
    }

    public RecordSet where(String key, Function<String, Boolean> fn) {
        checkKeyExists(singletonList(key));
        List<Record> filtered = records.stream().filter(rec -> fn.apply(accessor.of(rec).get(key))).collect(Collectors.toList());

        return new RecordSet(filtered, config);
    }

    public RecordSet where(Function<RecordWrapper<String>, Boolean> fn) {
        List<Record> filtered = records.stream().filter(rec -> fn.apply(accessor.of(rec))).collect(Collectors.toList());

        return new RecordSet(filtered, config);
    }

    public static RecordSet loadData(List<String> lines, RecConfig config) {
        SimpleParser parser = new SimpleParser(config.parseConfig());
        List<Record> records = lines.stream()
                .map(line -> Record.toRecord(parser.parse(line)))
                .collect(Collectors.toList());
        return new RecordSet(records, config);
    }

    public boolean contains(String key, String value) {
        checkKeyExists(singletonList(key));
        return indices.get(key).containsKey(value);
    }

    public RecordSet get(String key) {
        return select(key);
    }

    public void verify(Function<RecordWrapper<String>, Boolean> assertion) {
        ArrayList<Record> unexpected = new ArrayList<Record>();

        records.forEach(rec -> {
            if (assertion.apply(accessor.of(rec)))
                unexpected.add(rec);
        });

        unexpected.forEach(rec -> {
            System.out.printf("Unexpected record found: %s", rec);
        });
    }

    private RecConfig newType(List<String> keys, String providedName) {
        return new RecConfig() {
            @Override
            public String name() {
                return providedName != null? providedName : String.format("image of %s", config.name());
            }

            @Override
            public ParseConfig parseConfig() {
                return config.parseConfig();
            }

            @Override
            public String key() {
                return config.key();
            }

            @Override
            public String format() {
                return keys.stream().collect(Collectors.joining(String.valueOf(config.parseConfig().delimiter)));
            }

            @Override
            public Accessor<String> accessor() {
                return new Accessor<>(keys.toArray(new String[] {}));
            }
        };
    }

    private void checkKeyExists(List<String> keys) {
        if (!indices.keySet().containsAll(keys)) {
            throw new RuntimeException(String.format("Unexpected keys %s", keys));
        }
    }

    public boolean isUnique() {
        return records.stream().collect(toSet()).size() == records.size();
    }
}
