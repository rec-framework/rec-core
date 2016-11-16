package net.kimleo.rec.sepval.parser

import net.kimleo.rec.bind
import net.kimleo.rec.orElse
import net.kimleo.rec.sepval.SepValEntry

class SimpleParser(val config: ParseConfig = ParseConfig()) {

    fun parse(input: String): SepValEntry {

        val state = ParseState(input)
        val fields = arrayListOf<String>()
        while (!state.eof()) {
            fields.add(parseField(state))
        }

        return SepValEntry(fields, input)
    }

    private fun parseField(state: ParseState): String {
        val builder = StringBuilder()
        var c = state.current()
        while (c != null && c != config.delimiter) {
            if (builder.length == 0) {
                if (isSpace(c)) {
                    c = state.next()
                    continue
                }
                if (c == config.escape) {
                    return parseEscaped(state)
                }
            }
            builder.append(c)
            c = state.next()
        }
        state.next()
        return builder.toString().trim()
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

