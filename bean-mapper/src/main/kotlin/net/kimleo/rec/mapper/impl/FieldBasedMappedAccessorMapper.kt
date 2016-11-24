package net.kimleo.rec.mapper.impl

import net.kimleo.rec.bind
import net.kimleo.rec.concept.Mapped
import net.kimleo.rec.mapper.MappedAccessorMapper
import java.lang.reflect.Constructor

class FieldBasedMappedAccessorMapper<T>(val emptyCtor: Constructor<T>, val cls: Class<T>): MappedAccessorMapper<T> {
    override fun map(mapped: Mapped<String>): T {
        val instance = emptyCtor.newInstance()
        val keys = mapped.keys()
        for (key in keys) {
            val name = AccessorMapperBuilder.toStandardJavaName(key)
            cls.fields.find { it.name == name }.bind {
                it.set(instance, AccessorMapperBuilder.convertValue(mapped[key], it.type))
            }
        }
        return instance
    }
}