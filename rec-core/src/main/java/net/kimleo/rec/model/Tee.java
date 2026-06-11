package net.kimleo.rec.model;

import net.kimleo.rec.data.DataSet;

import java.util.stream.Stream;

public interface Tee {
    DataSet emit(DataSet record);

    default Source source() {
        return Source.from(Stream.empty());
    }

    default void to(Target target) {
        source().to(target);
    }
}
