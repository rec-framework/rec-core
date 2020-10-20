package net.kimleo.rec.v2.model;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Source<T> {
    Stream<T> stream();
    default Source<T> tee(Tee<T> tee) {
        return () -> this.stream().map(tee::emit);
    }

    default Source<T> filter(Predicate<T> predicate) {
        return () -> this.stream().filter(predicate);
    }

    default Source<T> skip(int n) {
        return from(stream().skip(n));
    }

    static <T> Source<T> from(Stream<T> records) {
        return () -> records;
    }

    default void to(Target<T> target) {
        target.putAll(this);
    }
}
