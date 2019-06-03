package net.kimleo.rec.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Table implements Iterable<Record> {
    final List<Cell> header;
    final List<Record> records;

    public Table(List<Record> records) {
        this(Collections.emptyList(), records);
    }

    public boolean hasHeader() {
        return !header.isEmpty();
    }

    public int size() {
        return records.size();
    }

    public Getters getters() {
        return Getters.from(header.stream()
                .map(Cell::getData)
                .map(Object::toString)
                .collect(Collectors.toList()));
    }

    public Record at(int cursor) {
        return records.get(cursor);
    }

    @Override
    public Iterator<Record> iterator() {
        return records.iterator();
    }
}
