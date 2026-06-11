package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.model.Tee;

import java.util.function.Function;

public class TransformTee implements Tee {

    private final Function<DataSet, DataSet> transformer;

    public TransformTee(Function<DataSet, DataSet> transformer) {
        this.transformer = transformer;
    }

    @Override
    public DataSet emit(DataSet record) {
        return transformer.apply(record);
    }
}
