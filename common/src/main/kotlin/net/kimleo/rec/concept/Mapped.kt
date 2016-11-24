package net.kimleo.rec.concept

interface Mapped<out T> {
    operator fun get(field: String): T?
    fun keys(): List<String>
}