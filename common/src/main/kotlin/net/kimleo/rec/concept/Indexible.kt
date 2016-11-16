package net.kimleo.rec.concept

interface Indexible<T> {
    operator fun get(index: Int): T
    val size: Int
}