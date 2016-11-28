package net.kimleo.rec.concept

interface QuerySelector<T> {
    fun findAll(repo: Queryable<T>): Queryable<T>
    fun findFirst(repo: Queryable<T>): T?
}