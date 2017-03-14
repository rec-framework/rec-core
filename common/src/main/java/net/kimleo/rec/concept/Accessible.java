package net.kimleo.rec.concept;

public interface Accessible<T> {
    T get(int index);

    int size();

}
