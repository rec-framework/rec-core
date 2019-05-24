package net.kimleo.rec.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
public class Table {
    List<Cell> header;
    List<Record> records;

    public Table(List<Record> records) {
        this(Collections.emptyList(), records);
    }

    public boolean hasHeader() {
        return !header.isEmpty();
    }

    public int size() {
        return records.size();
    }

    public Record at(int cursor) {
        return records.get(cursor);
    }
}
