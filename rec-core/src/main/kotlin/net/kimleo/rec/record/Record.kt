package net.kimleo.rec.record

import net.kimleo.rec.concept.Indexible
import net.kimleo.rec.orElse
import net.kimleo.rec.sepval.SepValEntry

data class Record(val cells: List<Cell>, val text: String, val original: Boolean = true): Indexible<String> {
    override val size = cells.size

    private var parent: Record? = null

    fun parent(): Record = parent.orElse { this }

    constructor(cells: List<Cell>, original: Record):
        this(cells, cells.map(Cell::value).joinToString(", "), false) {
        parent = original
    }

    override fun get(index: Int): String {
        return cells[index].value
    }

    override fun toString(): String {
        return text
    }
}

fun SepValEntry.toRecord() = Record(this.values.map(::Cell), this.source)
