package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.model.Tee;

import java.util.function.Predicate;

public class ItemCounterTee implements Tee {

    private final Predicate<DataSet> predicate;
    private int count;

    public ItemCounterTee(Predicate<DataSet> predicate) {
        this.predicate = predicate;
        this.count = 0;
    }

    @Override
    public DataSet emit(DataSet record) {
        if (predicate.test(record)) {
            count++;
        }
        return record;
    }

    public int getCount() {
        return count;
    }
}
