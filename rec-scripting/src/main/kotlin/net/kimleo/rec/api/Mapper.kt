package net.kimleo.rec.api

import net.kimleo.rec.mapper.impl.AccessorMapperBuilder
import net.kimleo.rec.repository.RecordSet
import net.kimleo.rec.repository.RecRepository
import kotlin.reflect.KClass

internal val builder = AccessorMapperBuilder()

fun <T: Any> RecordSet.map(kls: KClass<T>): List<T> {
    val mapper = builder.build(kls)

    return this.records
            .map { this.accessor.of(it) }
            .map { mapper.map(it) }
}

fun <T: Any> RecRepository.map(kls: KClass<T>): List<T> {
    return this.repo[kls.simpleName!!]?.map(kls)!!
}