package net.kimleo.rec.record

import net.kimleo.rec.concept.Indexible
import net.kimleo.rec.orElse
import net.kimleo.rec.sepval.SepValEntry

data class Record(val fields: List<Field>, val text: String, val original: Boolean = true): Indexible<String> {
    override val size = fields.size

    private var parent: Record? = null

    fun parent(): Record = parent.orElse { this }

    constructor(fields: List<Field>, original: Record):
        this(fields, fields.map(Field::value).joinToString(", "), false) {
        parent = original
    }

    override fun get(index: Int): String {
        return fields[index].value
    }
}

fun SepValEntry.toRecord() = Record(this.values.map(::Field), this.source)
