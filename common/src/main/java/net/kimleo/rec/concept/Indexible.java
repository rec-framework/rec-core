package net.kimleo.rec.concept;

public interface Indexible<T> {
    T get(int index);

    int size();

}
