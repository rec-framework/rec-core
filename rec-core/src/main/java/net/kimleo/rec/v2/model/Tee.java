package net.kimleo.rec.v2.model;

import net.kimleo.rec.concept.Mapped;

import java.util.stream.Stream;

public interface Tee {
    Mapped<String> emit(Mapped<String> record);

    default Source source() {
        return Source.from(Stream.empty());
    }
    default void to(Target target) {
        source().to(target);
    }
}
