package net.kimleo.rec.model;

import net.kimleo.rec.data.DataSet;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Source {
    Stream<DataSet> stream();

    default Source tee(Tee tee) {
        return () -> this.stream().map(tee::emit);
    }

    default Source filter(Predicate<DataSet> predicate) {
        return () -> this.stream().filter(predicate);
    }

    default Source skip(int n) {
        return from(stream().skip(n));
    }

    static Source from(Stream<DataSet> records) {
        return () -> records;
    }

    default void to(Target target) {
        target.putAll(this);
    }
}
