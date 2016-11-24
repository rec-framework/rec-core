package net.kimleo.rec.mapper.impl

import net.kimleo.rec.mapper.MappedAccessorMapper
import java.lang.reflect.Constructor
import kotlin.reflect.KClass

class AccessorMapperBuilder {
    fun <T: Any> build(kls: KClass<T>): MappedAccessorMapper<T> {
        val cls = kls.java
        val emptyCtor = cls.constructors.firstOrNull { it.parameterCount == 0 }
        if (emptyCtor != null) {
            return FieldBasedMappedAccessorMapper(emptyCtor as Constructor<T>, cls)
        } else {
            val nonemptyCtor = cls.constructors.first()
            kls.constructors.first()
            return ConstructorBasedMappedAccessorMapper(nonemptyCtor as Constructor<T>)
        }
    }

    companion object {
        fun toStandardJavaName(key: String): String {
            return key.trim().first().toLowerCase()+ key.split(" ")
                    .map(String::trim)
                    .map(String::capitalize)
                    .joinToString("").drop(1)
        }

        fun convertValue(s: String?, javaClass: Class<*>): Any {
            return when (javaClass) {
                Int::class.java ->  s?.toInt()
                Long::class.java -> s?.toLong()
                String::class.java -> s

                else -> null
            } as Any
        }

    }
}


fun String.javaName(): String {
    return AccessorMapperBuilder.toStandardJavaName(this);
}