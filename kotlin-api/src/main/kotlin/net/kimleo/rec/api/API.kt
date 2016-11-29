package net.kimleo.rec.api

import net.kimleo.rec.accessor.RecordWrapper
import net.kimleo.rec.loader.strategy.DefaultLoadingStrategy
import net.kimleo.rec.record.Record
import net.kimleo.rec.repository.RecRepository
import java.util.function.Predicate
import kotlin.reflect.KClass

private var globalName: RecRepository? = null

fun load(path: String): RecRepository =
        DefaultLoadingStrategy.repo(path)

class Rule(val name: String) {
    fun on(onSet: String, predicate: (RecordWrapper<String>) -> Boolean): (RecRepository) -> Unit {
        return {
            val set = it.from(name)
            set.forEach {
                if (!predicate.invoke(set.accessor.of(it)))
                    println("Test of rule $name on $onSet failed, record: [ $it ]")
            }
        }
    }

    fun <T: Any> on(onSet: KClass<T>, predicate: (T) -> Boolean): (RecRepository) -> Unit {
        return {
            val set = it.map(onSet)
            set.forEach {
                if (!predicate.invoke(it))
                    println("Test of rule $name on $onSet failed, record: [ $it ]")
            }
        }
    }
}

fun RecRepository.rule(name: String, on: String, predicate: (RecordWrapper<String>) -> Boolean) {
    return Rule(name).on(on, predicate).invoke(this)
}

fun <T: Any> RecRepository.rule(name: String, on: KClass<T>, predicate: (T) -> Boolean) {
    return Rule(name).on(on, predicate).invoke(this)
}
