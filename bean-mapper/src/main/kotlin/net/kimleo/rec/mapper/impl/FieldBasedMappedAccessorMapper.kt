package net.kimleo.rec.mapper.impl

import net.kimleo.rec.bind
import net.kimleo.rec.concept.Mapped
import net.kimleo.rec.mapper.MappedAccessorMapper
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.memberProperties

class FieldBasedMappedAccessorMapper<T : Any>(val emptyCtor: KFunction<T>, val cls: KClass<T>) : MappedAccessorMapper<T> {
    override fun map(mapped: Mapped<String>): T {
        val instance = emptyCtor.call()
        val keys = mapped.keys()
        for (key in keys) {
            val name = toStandardJavaName(key)
            cls.memberProperties.find { it.name == name }.bind {
                if (it is KMutableProperty<*>) {
                    val accessible = it.setter.isAccessible
                    it.setter.isAccessible = true
                    it.setter.call(instance, convertValue(mapped[key], it.returnType))
                    it.setter.isAccessible = accessible
                }
            }
        }
        return instance
    }
}