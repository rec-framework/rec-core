package net.kimleo.rec.concept;

public interface QuerySelector<T> {
    Queryable<T> findAll(Queryable<T> repo);

    T findFirst(Queryable<T> repo);
}