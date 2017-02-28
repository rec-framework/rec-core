package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.concept.Mapped;
import net.kimleo.rec.v2.model.Source;
import net.kimleo.rec.v2.model.Tee;

import java.util.Collection;

public class CollectTee implements Tee {
    private final Collection<Mapped<String>> collection;


    public CollectTee(Collection<Mapped<String>> collection) {
        this.collection = collection;
    }

    public Collection<Mapped<String>> collect() {
        return collection;
    }

    @Override
    public void emit(Mapped<String> record) {
        collection.add(record);
    }

    @Override
    public Source source() {
        return Source.from(collection.stream());
    }
}
