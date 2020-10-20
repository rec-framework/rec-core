package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.v2.model.Tee;

import java.util.function.Function;

public class TransformTee<T> implements Tee<T>{

    private final Function<T, T> transformer;

    public TransformTee(Function<T, T> transformer) {
        this.transformer = transformer;
    }

    @Override
    public T emit(T record) {
        return transformer.apply(record);
    }
}
