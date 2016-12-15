package net.kimleo.rec;

public class Pair<K, V> {
    public final K first;
    public final V second;


    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

    public K component1() {
        return first;
    }

    public V component2() {
        return second;
    }
}
