package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.v2.model.Tee;

import java.util.function.Predicate;

public class ItemCounterTee<T> implements Tee<T> {

    private final Predicate<T> predicate;
    private int count;

    public ItemCounterTee(Predicate<T> predicate) {
        this.predicate = predicate;
        this.count = 0;
    }

    @Override
    public T emit(T record) {
        if(predicate.test(record)) {
            count ++;
        }
        return record;
    }

    public int getCount() {
        return count;
    }
}
