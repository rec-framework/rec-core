package net.kimleo.rec.v2.model;

import net.kimleo.rec.concept.Mapped;

import java.util.stream.Stream;

public interface Tee<T> {
    T emit(T record);

    default Source<T> source() {
        return Source.from(Stream.empty());
    }
    default void to(Target target) {
        source().to(target);
    }
}
