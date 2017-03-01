package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.concept.Mapped;
import net.kimleo.rec.v2.model.Tee;

import java.util.function.Predicate;

public class ItemCounterTee implements Tee {

    private final Predicate<Mapped<String>> predicate;
    private int count;

    public ItemCounterTee(Predicate<Mapped<String>> predicate) {
        this.predicate = predicate;
        this.count = 0;
    }

    @Override
    public Mapped<String> emit(Mapped<String> record) {
        if(predicate.test(record)) {
            count ++;
        }
        return record;
    }

    public int count() {
        return count;
    }
}
