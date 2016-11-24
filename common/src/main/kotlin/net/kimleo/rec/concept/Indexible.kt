package net.kimleo.rec.concept

interface Indexible<out T> {
    operator fun get(index: Int): T
    val size: Int
}