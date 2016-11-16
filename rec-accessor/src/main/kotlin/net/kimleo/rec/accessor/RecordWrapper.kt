package net.kimleo.rec.accessor

data class RecordWrapper<T>(val fieldNames: Map<String, Int>, val record: Indexible<T>) {

    operator fun get(field: String): T? {
        if (fieldNames.containsKey(field)) {
            return getByIndex(fieldNames[field]!!, record)
        }
        return null
    }

    private fun getByIndex(index: Int, record: Indexible<T>): T? {
        if (index >= 0) {
            return record[index]
        } else {
            return record[record.size + index]
        }
    }
}