package net.kimleo.rec.v2.model;

public interface Target<T> {
    void put(T record);

    default Target<T> tee(Tee<T> tee) {
        return record -> this.put(tee.emit(record));
    }

    default void putAll(Source<T> source) {
        source.stream().forEach(this::put);
    }

}
