package net.kimleo.rec.accessor

import net.kimleo.rec.accessor.lexer.Lexer
import net.kimleo.rec.record.Record
import net.kimleo.rec.record.Field

class AccessorFactory(fields: Array<out String>) {
    val fieldMapPair = Lexer.buildFieldMapPair(*fields)
    val fieldMap = fieldMapPair.first
    val leastCapacity = fieldMapPair.second

    constructor(record: Record): this(record.fields.map(Field::value).toTypedArray()) {}

    fun create(record: Record): Accessor {
        assert(record.size >= leastCapacity)

        return Accessor(fieldMap, record)
    }

    fun of(record: Record) = create(record)
}