package net.kimleo.rec.common.concept;

public interface Accessible<T> {
    T get(int index);

    int size();

}
