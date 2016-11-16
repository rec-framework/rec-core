package net.kimleo.rec.sepval.parser


class ParseState(val input: String) {
    val size = input.length
    var index = 0

    fun current(): Char? {
        return if (eof()) null else input[index]
    }

    fun eof(): Boolean {
        return index >= size
    }

    fun next(): Char? {
        index ++
        return if (eof()) null else current()
    }
}
