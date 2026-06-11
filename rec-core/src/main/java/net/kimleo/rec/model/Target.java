package net.kimleo.rec.model;

import net.kimleo.rec.data.DataSet;

public interface Target {
    void put(DataSet record);

    default Target tee(Tee tee) {
        return record -> this.put(tee.emit(record));
    }

    default void putAll(Source source) {
        source.stream().forEach(this::put);
    }
}
