package net.kimleo.rec

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import kotlin.system.exitProcess

internal class Dummy {}

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

fun lines(file: String): List<String> {
    val reader = BufferedReader(InputStreamReader(FileInputStream(File(file))))

    val lines = reader.readLines()
    reader.close()
    return lines
}

fun linesOfRes(file: String): List<String> {
    val stream = Dummy::class.java.classLoader.getResourceAsStream(file)
    val reader = BufferedReader(InputStreamReader(stream))

    val lines = reader.readLines()
    reader.close()
    return lines
}
