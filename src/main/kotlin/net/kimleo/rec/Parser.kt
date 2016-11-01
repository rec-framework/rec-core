package net.kimleo.rec


data class Configuration(val delimiter: Char = ',', val escape: Char? = '\"')

class SimpleParser(val config: Configuration = Configuration()) {

    fun parse(input: String): Tuple? {

        val state = ParseState(input)
        val fields = arrayListOf<Field>()
        while (!state.eof()) {
            fields.add(parseField(state)!!)
        }

        return Tuple(fields)
    }

    private fun parseField(state: ParseState): Field? {
        val builder = StringBuilder()
        var c = state.current()
        while (c != null && c != config.delimiter) {
            if (builder.length == 0) {
                if (isSpace(c)) {
                    c = state.next()
                    continue
                }
                if (c == config.escape) {
                    return Field(parseEscaped(state))
                }
            }
            builder.append(c)
            c = state.next()
        }
        state.next()
        return Field(builder.toString().trim())
    }

    private fun isSpace(c: Char?): Boolean {
        return c.bind { c == ' ' || c == '\t' } .orElse { false }
    }

    private fun parseEscaped(state: ParseState): String {
        val builder = StringBuilder()
        assert(state.current() == config.escape)
        var c = state.next()
        while (!state.eof() || c != config.delimiter) {
            if (c == config.escape) {
                c = state.next()
                if (c != config.escape) {
                    expectDelimiter(state)
                    state.next()
                    break
                }
            }
            builder.append(c)
            c = state.next()
        }
        return builder.toString()
    }

    private fun expectDelimiter(state: ParseState) {
        while (isSpace(state.current())) {
            state.next()
        }
        assert(state.current() == null || state.current() == config.delimiter) {
            "Expect delimiter but found `${state.current()}`"
        }
    }

}

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
