package net.kimleo.rec.mapper.impl

import net.kimleo.rec.mapper.MappedAccessorMapper
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.defaultType

class AccessorMapperBuilder {
    fun <T: Any> build(kls: KClass<T>): MappedAccessorMapper<T> {
        val cls = kls.java
        val emptyCtor = kls.constructors.firstOrNull { it.parameters.size == 0 }
        if (emptyCtor != null) {
            return FieldBasedMappedAccessorMapper(emptyCtor, kls)
        } else {
            val nonemptyCtor = kls.constructors.first()
            return ConstructorBasedMappedAccessorMapper(nonemptyCtor, kls)
        }
    }
}


fun String.javaName(): String {
    return toStandardJavaName(this);
}

fun toStandardJavaName(key: String): String {
    return key.trim().first().toLowerCase()+ key.split(" ")
            .map(String::trim)
            .map(String::capitalize)
            .joinToString("").drop(1)
}

fun convertValue(s: String?, type: KType): Any? {
    var typeName = type.toString()

    if (type.isMarkedNullable || typeName.endsWith("!")) {
        typeName = typeName.trimEnd('?').trimEnd('!')
    }
    return when (typeName) {
        Int::class.defaultType.toString() ->  s?.toInt()
        Long::class.defaultType.toString() -> s?.toLong()
        String::class.defaultType.toString() -> s?.toString()
        else -> null
    }
}