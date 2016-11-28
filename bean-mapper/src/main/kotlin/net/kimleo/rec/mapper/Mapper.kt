package net.kimleo.rec.mapper

interface Mapper<in V, out T> {
    fun map(mapped: V): T
}