package net.kimleo.rec.concept

interface Queryable<T> {
    fun select(selector: QuerySelector<T>): Queryable<T> = selector.findAll(this)
    fun select1(selector: QuerySelector<T>): T? = selector.findOne(this)
    fun where(selector: QuerySelector<T>, fn: T.() -> Boolean): Queryable<T>
}