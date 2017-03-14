package net.kimleo.rec.v2.model;

import net.kimleo.rec.concept.Mapped;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Source {
    Stream<Mapped<String>> stream();
    default Source tee(Tee tee) {
        return () -> this.stream().map(tee::emit);
    }

    default Source filter(Predicate<Mapped<String>> predicate) {
        return () -> this.stream().filter(predicate);
    }

    static Source from(Stream<Mapped<String>> records) {
        return () -> records;
    }

    default void to(Target target) {
        target.putAll(this);
    }
}
