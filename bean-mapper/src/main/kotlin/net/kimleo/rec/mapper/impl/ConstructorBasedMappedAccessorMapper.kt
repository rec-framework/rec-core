package net.kimleo.rec.mapper.impl

import net.kimleo.rec.bind
import net.kimleo.rec.concept.Mapped
import net.kimleo.rec.mapper.MappedAccessorMapper
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

class ConstructorBasedMappedAccessorMapper<T: Any>(val nonemptyCtor: KFunction<T>, val kls: KClass<T>): MappedAccessorMapper<T> {
    override fun map(mapped: Mapped<String>): T {
        val params = nonemptyCtor.parameters
        val paramsValue = hashMapOf<KParameter, Any?>()
        for (key in mapped.keys()) {
            val name = toStandardJavaName(key)
            params.find {
                it.name == name
            }.bind {
                paramsValue[it] = convertValue(mapped[key], it.type)
            }
        }
        return nonemptyCtor.callBy(paramsValue)
    }
}