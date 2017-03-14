package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.concept.Mapped;
import net.kimleo.rec.v2.model.Tee;

import java.util.function.Function;

public class TransformTee implements Tee{

    private final Function<Mapped<String>, Mapped<String>> transformer;

    public TransformTee(Function<Mapped<String>, Mapped<String>> transformer) {
        this.transformer = transformer;
    }

    @Override
    public Mapped<String> emit(Mapped<String> record) {
        return transformer.apply(record);
    }
}
