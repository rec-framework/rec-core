package net.kimleo.rec.accessor

import net.kimleo.rec.accessor.lexer.Lexer
import net.kimleo.rec.concept.Indexible

class Accessor<T>(fields: Array<out String>) {
    val fieldMapPair = Lexer.buildFieldMapPair(*fields)
    val fieldMap = fieldMapPair.first
    val leastCapacity = fieldMapPair.second

    fun create(record: Indexible<T>): RecordWrapper<T> {
        assert(record.size >= leastCapacity)

        return RecordWrapper(fieldMap, record)
    }

    fun of(record: Indexible<T>) = create(record)
}