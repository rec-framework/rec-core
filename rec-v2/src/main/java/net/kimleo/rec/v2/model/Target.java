package net.kimleo.rec.v2.model;

import net.kimleo.rec.concept.Mapped;

public interface Target {
    void put(Mapped<String> record);

    default Target tee(Tee tee) {
        return record -> {
            tee.emit(record);
            this.put(record);
        };
    }

    default void putAll(Source source) {
        source.stream().forEach(this::put);
    }

    default void close() {

    }
}
