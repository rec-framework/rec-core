package net.kimleo.rec.sepval

data class SepValEntry(val values: List<String>, val source: String) {

    val size = values.size

    operator fun get(index: Int) = values.get(index)
}