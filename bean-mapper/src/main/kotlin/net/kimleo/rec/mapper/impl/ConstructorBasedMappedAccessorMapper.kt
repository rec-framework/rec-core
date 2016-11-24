package net.kimleo.rec.mapper.impl

import net.kimleo.rec.bind
import net.kimleo.rec.concept.Mapped
import net.kimleo.rec.mapper.MappedAccessorMapper
import java.lang.reflect.Constructor

class ConstructorBasedMappedAccessorMapper<T>(val nonemptyCtor: Constructor<T>): MappedAccessorMapper<T> {
    override fun map(mapped: Mapped<String>): T {
        val params = nonemptyCtor.parameters
        val paramsValue = hashMapOf<Class<*>, Any>()
        for (key in mapped.keys()) {
            val name = AccessorMapperBuilder.toStandardJavaName(key)
            params.find {
                it.name == name
            }.bind {
                paramsValue[it.type] = AccessorMapperBuilder.convertValue(mapped[key], it.type)
            }
        }
        return nonemptyCtor.newInstance(*(params.map { paramsValue[it.type] }.toTypedArray()))
    }
}