package net.kimleo.rec.concept

interface Queryable<T> {
    fun select(selector: QuerySelector<T>): Queryable<T>
    fun select1(selector: QuerySelector<T>): Queryable<T>
}