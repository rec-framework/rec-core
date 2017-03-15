package net.kimleo.rec.v2.accessor;

import net.kimleo.rec.Pair;
import net.kimleo.rec.v2.accessor.lexer.Lexer;
import net.kimleo.rec.concept.Accessible;

import java.util.Map;

public class Accessor<T> {

    private final Map<String, Integer> fieldMap;
    private final Integer leastCapacity;

    public Accessor(String[] fields) {
        Pair<Map<String, Integer>, Integer> fieldMapPair = Lexer.buildFieldMapPair(fields);
        fieldMap = fieldMapPair.getFirst();
        leastCapacity = fieldMapPair.getSecond();
    }

    public Map<String, Integer> getFieldMap() {
        return fieldMap;
    }

    public Integer getLeastCapacity() {
        return leastCapacity;
    }

    public RecordWrapper<T> create(Accessible<T> record) {
        assert (record.size() >= leastCapacity);

        return new RecordWrapper<>(fieldMap, record);
    }

    public RecordWrapper<T> of(Accessible<T> record) {
        return create(record);
    }
}
