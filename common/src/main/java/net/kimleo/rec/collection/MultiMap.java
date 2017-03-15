package net.kimleo.rec.collection;

import java.util.Collection;
import java.util.Map;

public interface MultiMap<K, V> extends Map<K, Collection<V>> {
    int keyCount();

    int valueCount();

    V put1(K key, V value);
}
