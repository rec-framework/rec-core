package net.kimleo.rec.accessor;

import net.kimleo.rec.concept.Indexible;
import net.kimleo.rec.concept.Mapped;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecordWrapper<T> implements Indexible<T>, Mapped<T> {

    private final Map<String, Integer> fieldNames;

    private final Indexible<T> record;

    public RecordWrapper(Map<String, Integer> fieldNames, Indexible<T> record) {
        this.fieldNames = fieldNames;
        this.record = record;
    }

    @Override
    public T get(int index) {
        return getByIndex(index, record);
    }

    @Override
    public int getSize() {
        return record.getSize();
    }

    @Override
    public T get(String field) {
        if (fieldNames.containsKey(field)) {
            return getByIndex(fieldNames.get(field), record);
        }
        return null;
    }

    private T getByIndex(Integer index, Indexible<T> record) {
        if (index >= 0) {
            return record.get(index);
        } else {
            return record.get(record.getSize() + index);
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
