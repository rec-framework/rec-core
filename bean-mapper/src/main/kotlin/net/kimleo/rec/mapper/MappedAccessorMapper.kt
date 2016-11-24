package net.kimleo.rec.mapper

import net.kimleo.rec.concept.Mapped

interface MappedAccessorMapper<out T>: Mapper<Mapped<String>, T> {
}