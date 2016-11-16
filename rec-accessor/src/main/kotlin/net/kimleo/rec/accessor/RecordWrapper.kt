package net.kimleo.rec.accessor

import net.kimleo.rec.concept.Indexible

data class RecordWrapper<T>(val fieldNames: Map<String, Int>, val record: Indexible<T>): Indexible<T> {
    operator fun get(field: String): T? {
        if (fieldNames.containsKey(field)) {
            return getByIndex(fieldNames[field]!!, record)
        }
        return null
    }

    override fun get(index: Int): T {
        return record[index]
    }

    override val size = record.size

    private fun getByIndex(index: Int, record: Indexible<T>): T? {
        if (index >= 0) {
            return record[index]
        } else {
            return record[record.size + index]
        }
    }
}