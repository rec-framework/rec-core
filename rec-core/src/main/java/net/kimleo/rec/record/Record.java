package net.kimleo.rec.record;

import net.kimleo.rec.concept.Indexible;
import net.kimleo.rec.repository.RecConfig;
import net.kimleo.rec.sepval.SepValEntry;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Record implements Indexible<String> {
    private final List<String> cells;

    private final String text;

    private final boolean original;

    private Record parent = null;

    Record(List<String> cells, String text, boolean original) {
        this.cells = cells;
        this.text = text;
        this.original = original;
    }

    public Record(List<String> cells, Record original) {
        this(cells, cells.stream().collect(Collectors.joining(", ")), false);
        this.parent = original;
    }

    @Override
    public String get(int index) {
        return cells.get(index);
    }

    @Override
    public int size() {
        return cells.size();
    }

    @Override
    public String toString() { return text; }

    public Record parent() {
        if (parent == null) return this;
        return parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        return cells != null ? cells.equals(record.cells) : record.cells == null;
    }

    public String getText() {
        return text;
    }

    public List<String> getCells() {
        return cells;
    }

    @Override
    public int hashCode() {
        return cells != null ? cells.hashCode() : 0;
    }


    public static Record toRecord(SepValEntry sepVal) {
        return new Record(sepVal.getValues(), sepVal.getSource(), true);

    }
}
