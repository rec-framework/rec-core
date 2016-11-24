package net.kimleo.rec.mapper

import net.kimleo.rec.concept.Mapped

interface Mapper<in V, out T> {
    fun map(mapped: V): T
}