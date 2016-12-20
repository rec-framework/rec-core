package net.kimleo.rec.concept;

import java.util.List;

public interface Mapped<T> {
    T get(String field);

    List<String> keys();
}
