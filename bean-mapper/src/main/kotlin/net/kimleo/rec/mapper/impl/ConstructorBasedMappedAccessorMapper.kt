package net.kimleo.rec.mapper.impl

import net.kimleo.rec.bind
import net.kimleo.rec.concept.Mapped
import net.kimleo.rec.mapper.MappedAccessorMapper
import kotlin.reflect.KFunction

class ConstructorBasedMappedAccessorMapper<T>(val nonemptyCtor: KFunction<T>): MappedAccessorMapper<T> {
    override fun map(mapped: Mapped<String>): T {
        val params = nonemptyCtor.parameters
        val paramsValue = hashMapOf<String, Any?>()
        for (key in mapped.keys()) {
            val name = toStandardJavaName(key)
            params.find {
                it.name == name
            }.bind {
                paramsValue[it.name!!] = convertValue(mapped[key], it.type)
            }
        }
        return nonemptyCtor.call(*(params.map { paramsValue[it.name] }.toTypedArray()))
    }
}