package net.kimleo.rec.sepval;

import net.kimleo.rec.concept.Indexible;

import java.util.List;

public class SepValEntry implements Indexible<String> {

    private final List<String> values;
    private final String source;

    public SepValEntry(List<String> values, String source) {
        this.values = values;
        this.source = source;
    }

    @Override
    public String get(int index) {
        return values.get(index);
    }

    @Override
    public int size() {
        return values.size();
    }

    public List<String> getValues() {
        return values;
    }

    public String getSource() {
        return source;
    }
}
