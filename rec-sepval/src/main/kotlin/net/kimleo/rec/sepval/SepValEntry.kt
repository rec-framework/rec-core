package net.kimleo.rec.sepval

import net.kimleo.rec.concept.Indexible

data class SepValEntry(val values: List<String>, val source: String): Indexible<String> {

    override val size = values.size

    override operator fun get(index: Int) = values.get(index)
}