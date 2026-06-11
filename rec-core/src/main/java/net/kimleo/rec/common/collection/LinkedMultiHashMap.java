package net.kimleo.rec.common.collection;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class LinkedMultiHashMap<K, V> implements MultiMap<K, V> {

    private final Map<K, Collection<V>> map = new LinkedHashMap<>();

    @Override
    public int size() {
        int sum = 0;
        for (Collection<V> vs : map.values()) {
            if (vs != null) {
                sum += vs.size();
            }
        }
        return sum;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        for (Collection<V> vs : map.values()) {
            if (vs != null && vs.contains(value))
                return true;
        }
        return false;
    }

    @Override
    public Collection<V> get(Object key) {
        return map.get(key);
    }

    @Override
    public Collection<V> put(K key, Collection<V> value) {
        if (map.containsKey(key)) {
            map.get(key).addAll(value);
        } else {
            map.put(key, value);
        }
        return value;
    }

    @Override
    public Collection<V> remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Collection<V>> m) {
        for (K key : m.keySet()) {
            if (map.containsKey(key)) {
                map.get(key).addAll(m.get(key));
            } else {
                map.put(key, m.get(key));
            }
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Collection<V>> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, Collection<V>>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LinkedMultiHashMap && map.equals(((LinkedMultiHashMap) o).map);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public int keyCount() {
        return map.size();
    }

    @Override
    public int valueCount() {
        return size();
    }

    @Override
    public V put1(K key, V value) {
        if (map.containsKey(key)) {
            map.get(key).add(value);
        } else {
            Set<V> vs = new HashSet<>();
            vs.add(value);
            map.put(key, vs);
        }
        return value;
    }

    public static <V, U> MultiMap<V, U> from(Stream<V> collection, Function<V, U> transform) {
        LinkedMultiHashMap<V, U> multiMap = new LinkedMultiHashMap<>();
        collection.forEach(item -> multiMap.put1(item, transform.apply(item)));

        return multiMap;
    }
}


