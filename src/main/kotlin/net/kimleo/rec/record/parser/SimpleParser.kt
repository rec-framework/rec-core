package net.kimleo.rec.record.parser

import net.kimleo.rec.bind
import net.kimleo.rec.orElse
import net.kimleo.rec.record.Field
import net.kimleo.rec.record.Record

class SimpleParser(val config: ParseConfig = ParseConfig()) {

    fun parse(input: String): Record {

        val state = ParseState(input)
        val fields = arrayListOf<Field>()
        while (!state.eof()) {
            fields.add(parseField(state))
        }

        return Record(fields, input)
    }

    private fun parseField(state: ParseState): Field {
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