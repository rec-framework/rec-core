package net.kimleo.rec

import kotlin.system.exitProcess

inline fun <T, U> T?.bind(fn: (T) -> U?): U? =
        if (this == null) null else fn.invoke(this)

inline fun <T> T?.orElse(fn: () -> T) =
        if (this != null) this else fn.invoke()

inline fun Boolean.orElse(fn: () -> Boolean) = this || fn.invoke()

inline fun Boolean.andThen(fn: () -> Boolean) = this && fn.invoke()

fun exit(message: String, code: Int = -1) {
    println(message)
    exitProcess(code)
}
