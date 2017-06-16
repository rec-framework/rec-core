package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.concept.Mapped;
import net.kimleo.rec.v2.model.Source;
import net.kimleo.rec.v2.model.Tee;

import java.util.Collection;

public class CollectTee<T> implements Tee<T> {
    private final Collection<T> collection;


    public CollectTee(Collection<T> collection) {
        this.collection = collection;
    }

    public Collection<T> collect() {
        return collection;
    }

    @Override
    public T emit(T record) {
        collection.add(record);
        return record;
    }

    @Override
    public Source<T> source() {
        return Source.from(collection.stream());
    }
}
