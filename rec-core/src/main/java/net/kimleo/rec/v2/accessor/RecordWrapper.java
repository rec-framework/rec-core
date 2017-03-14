package net.kimleo.rec.v2.accessor;

import net.kimleo.rec.concept.Accessible;
import net.kimleo.rec.concept.Mapped;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecordWrapper<T> implements Accessible<T>, Mapped<T> {

    private final Map<String, Integer> fieldNames;

    private final Accessible<T> record;

    public RecordWrapper(Map<String, Integer> fieldNames, Accessible<T> record) {
        this.fieldNames = fieldNames;
        this.record = record;
    }

    @Override
    public T get(int index) {
        return getByIndex(index, record);
    }

    @Override
    public int size() {
        return record.size();
    }

    @Override
    public T get(String field) {
        if (fieldNames.containsKey(field)) {
            return getByIndex(fieldNames.get(field), record);
        }
        return null;
    }

    private T getByIndex(Integer index, Accessible<T> record) {
        if (index >= 0) {
            return record.get(index);
        } else {
            return record.get(record.size() + index);
        }
    }

    @Override
    public List<String> keys() {
        return fieldNames.keySet().stream().collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return record.toString();
    }
}
