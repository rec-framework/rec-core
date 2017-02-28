package net.kimleo.rec.v2.model;

import net.kimleo.rec.concept.Mapped;

import java.util.stream.Stream;

public interface Source {
    Stream<Mapped<String>> stream();
    default Source tee(Tee tee) {
        return () -> this.stream().map(it -> {
            tee.emit(it); return it;});
    }

    static Source from(Stream<Mapped<String>> records) {
        return () -> records;
    }

    default void to(Target target) {
        target.putAll(this);
    }
}
