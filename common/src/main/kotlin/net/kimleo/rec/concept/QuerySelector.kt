package net.kimleo.rec.concept

interface QuerySelector<T> {
    fun findAll(repo: Queryable<T>): Queryable<T>
    fun findOne(repo: Queryable<T>): T?
}