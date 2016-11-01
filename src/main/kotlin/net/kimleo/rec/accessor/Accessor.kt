package net.kimleo.rec.accessor

import net.kimleo.rec.record.Record

data class Accessor(val fields: Map<String, Int>, val record: Record) {
    val size = fields.size

    fun get(field: String): String? {
        if (fields.containsKey(field)) {
            return getByIndex(fields[field]!!, record)
        }
        return null
    }

    private fun getByIndex(index: Int, record: Record): String? {
        if (index >= 0) {
            return record.fields[index].value
        } else {
            return record.fields[record.size + index].value
        }
    }
}