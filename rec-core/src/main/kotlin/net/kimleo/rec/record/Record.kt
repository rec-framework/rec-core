package net.kimleo.rec.record

import net.kimleo.rec.accessor.Indexible

data class Record(val fields: List<Field>, val text: String): Indexible<Field> {
    val size = fields.size

    constructor(fields: List<Field>): this(fields, fields.map(Field::value).joinToString(", ")) {}

    override fun get(index: Int): Field {
        return fields[index]
    }
}