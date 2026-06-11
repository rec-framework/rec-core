package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.model.Source;
import net.kimleo.rec.model.Tee;

import java.util.ArrayList;
import java.util.List;

public class InMemoryCacheTee implements Tee {

    private final List<DataSet> cache;
    private final int maxSize;

    public InMemoryCacheTee(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new ArrayList<>(maxSize > 0 ? maxSize : 256);
    }

    public InMemoryCacheTee() {
        this(-1);
    }

    @Override
    public DataSet emit(DataSet record) {
        if (maxSize > 0 && cache.size() >= maxSize) {
            cache.remove(0);
        }
        cache.add(record);
        return record;
    }

    @Override
    public Source source() {
        return () -> new ArrayList<>(cache).stream();
    }

    public int size() {
        return cache.size();
    }

    public void clear() {
        cache.clear();
    }
}
