package net.kimleo.rec.accessor

interface Indexible<T> {
    fun get(index: Int): T
}