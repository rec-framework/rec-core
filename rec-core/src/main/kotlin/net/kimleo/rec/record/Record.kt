package net.kimleo.rec.record

import net.kimleo.rec.accessor.Indexible
import net.kimleo.rec.sepval.SepValEntry

data class Record(val fields: List<Field>, val text: String): Indexible<String> {
    override val size = fields.size

    constructor(fields: List<Field>): this(fields, fields.map(Field::value).joinToString(", ")) {}

    override fun get(index: Int): String {
        return fields[index].value
    }
}

fun SepValEntry.toRecord() = Record(this.values.map(::Field), this.source)