package net.kimleo.rec.record

import net.kimleo.rec.accessor.AccessorFactory
import net.kimleo.rec.orElse
import net.kimleo.rec.record.Record
import net.kimleo.rec.record.RecordType

class RecordCollection(val records: List<Record>, val type: RecordType): Iterable<Record> {
    override fun iterator(): Iterator<Record> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val accessor = type.accessor

    fun select(vararg keys: String): RecordCollection {
        checkKeyExists(*keys)
        val newType = newType(keys)
        val newRecords = arrayListOf<Record>()
        for (record in records) {
            val fields = arrayListOf<String>()
            val accessor = accessor.of(record)
            for (key in keys) {
                fields.add(accessor.get(key).orEmpty())
            }
            newRecords.add(Record(fields.map(::Field)))
        }

        return RecordCollection(newRecords, newType)
    }

    fun where(key: String, pattern: String): RecordCollection {
        val filtered = records.filter { rec ->
            accessor.of(rec).get(key)?.contains(pattern).orElse { false }
        }
        return RecordCollection(filtered, type)
    }

    fun where(key: String, pattern: Regex): RecordCollection {
        val filtered = records.filter { rec ->
            accessor.of(rec).get(key)?.contains(pattern).orElse { false }
        }
        return RecordCollection(filtered, type)
    }

    private fun checkKeyExists(vararg keys: String) {
        for (key in keys) {
            assert(key in accessor.fieldMap.keys)
        }
    }


    private fun newType(keys: Array<out String>): RecordType {
        return object: RecordType {
            override val name = "select of ${type.name}"
            override val configuration = type.configuration
            override val key = type.key
            override val format = keys.joinToString(type.configuration.delimiter.toString())
            override val accessor = AccessorFactory(Record(keys.map(::Field)))
        }
    }
}