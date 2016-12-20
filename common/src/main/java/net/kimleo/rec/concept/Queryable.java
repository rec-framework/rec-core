package net.kimleo.rec.concept;

import java.util.function.Function;

public interface Queryable<T> {

    default Queryable<T> select(QuerySelector<T> selector) {
        return selector.findAll(this);
    }

    default T select1(QuerySelector<T> selector) {
        return selector.findFirst(this);
    }

    Queryable<T> where(QuerySelector<T> selector, Function<T, Boolean> fn);
}