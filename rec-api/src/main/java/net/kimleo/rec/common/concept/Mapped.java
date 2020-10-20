package net.kimleo.rec.common.concept;

import java.util.List;

public interface Mapped<T> {
    T get(String field);

    List<String> keys();
}
