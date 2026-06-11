package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.model.Source;
import net.kimleo.rec.model.Tee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectTee implements Tee {
    private final List<DataSet> collection;

    public CollectTee() {
        this.collection = new ArrayList<>();
    }

    public CollectTee(Collection<DataSet> collection) {
        this.collection = new ArrayList<>(collection);
    }

    public List<DataSet> collect() {
        return collection;
    }

    @Override
    public DataSet emit(DataSet record) {
        collection.add(record);
        return record;
    }

    @Override
    public Source source() {
        return Source.from(collection.stream());
    }
}
