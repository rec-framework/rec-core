package net.kimleo.rec.v2.model;

import net.kimleo.rec.concept.Mapped;

public interface Target {
    void put(Mapped<String> record);

    default Target tee(Tee tee) {
        return record -> this.put(tee.emit(record));
    }

    default void putAll(Source source) {
        source.stream().forEach(this::put);
    }

}
