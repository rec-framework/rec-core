package net.kimleo.rec.record

import net.kimleo.rec.accessor.Accessor
import net.kimleo.rec.bind
import net.kimleo.rec.orElse
import net.kimleo.rec.record.parser.SimpleParser

class RecCollection(val records: List<Record>, val type: RecType): Iterable<Record> {
    override fun iterator(): Iterator<Record> {
        return records.iterator()
    }

    val accessor = type.accessor

    fun select(keys: List<String>, name: String? = null): RecCollection {
        checkKeyExists(*keys.toTypedArray())
        val newType = newType(keys, name)
        val newRecords = arrayListOf<Record>()
        for (record in records) {
            val fields = arrayListOf<String>()
            val accessor = accessor.of(record)
            for (key in keys) {
                fields.add(accessor[key].orEmpty())
            }
            newRecords.add(Record(fields.map(::Field)))
        }

        return RecCollection(newRecords, newType)
    }

    fun select(vararg keys: String): RecCollection {
        return select(keys.toList())
    }

    fun where(key: String, assertion: String.() -> Boolean): RecCollection {
        checkKeyExists(key)
        val filtered = records.filter { rec ->
            accessor.of(rec)[key].bind(assertion).orElse { false }
        }
        return RecCollection(filtered, type)
    }

    companion object {
        fun loadData(lines: List<String>, type: RecType): RecCollection {
            val parser = SimpleParser(type.parseConfig)
            val records = arrayListOf<Record>()
            for (line in lines) {
                val record = parser.parse(line)
                record.bind { records.add(it) }
            }

            return RecCollection(records, type)
        }
    }

    fun isUnique(): Boolean {
        return records.toSet().size == records.size
    }


    operator fun get(name: String): RecCollection {
        return select(name)
    }

    private fun checkKeyExists(vararg keys: String) {
        for (key in keys) {
            assert(key in accessor.fieldMap.keys)
        }
    }

    private fun newType(keys: List<String>, providedName: String? = null): RecType {
        return object: RecType {
            override val name = providedName.orElse { "image of ${type.name}" }
            override val parseConfig = type.parseConfig
            override val key = type.key
            override val format = keys.joinToString(type.parseConfig.delimiter.toString())
            override val accessor = Accessor(Record(keys.map(::Field)))
        }
    }
}