package net.kimleo.rec.accessor

interface Indexible<T> {
    operator fun get(index: Int): T
    val size: Int
}