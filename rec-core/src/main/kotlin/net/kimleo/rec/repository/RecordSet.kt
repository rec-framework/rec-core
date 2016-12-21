package net.kimleo.rec.repository

import net.kimleo.rec.accessor.Accessor
import net.kimleo.rec.accessor.RecordWrapper
import net.kimleo.rec.bind
import net.kimleo.rec.collection.LinkedMultiHashMap
import net.kimleo.rec.collection.MultiMap
import net.kimleo.rec.orElse
import net.kimleo.rec.record.Cell
import net.kimleo.rec.record.Record
import net.kimleo.rec.record.Record.toRecord
import net.kimleo.rec.sepval.parser.SimpleParser

class RecordSet(val records: List<Record>, val config: RecConfig): Iterable<Record> {

    val indices = buildIndex(records, config.accessor())

    private fun buildIndex(records: List<Record>, accessor: Accessor<String>): Map<String, MultiMap<String, Record>> {
        val indices = hashMapOf<String, MultiMap<String, Record>>()
        for (key in accessor.fieldMap.keys) {
            val index = LinkedMultiHashMap<String, Record>()
            for (record in records) {
                index.put1(accessor.of(record)[key], record)
            }
            indices[key] = index
        }
        return indices
    }

    override fun iterator(): Iterator<Record> {
        return records.iterator()
    }

    val accessor = config.accessor()!!

    fun select(keys: List<String>, name: String? = null): RecordSet {
        checkKeyExists(*keys.toTypedArray())
        val newType = newType(keys, name)
        val newRecords = arrayListOf<Record>()
        for (record in records) {
            val fields = arrayListOf<String>()
            val accessor = accessor.of(record)
            for (key in keys) {
                fields.add(accessor[key].orEmpty())
            }
            newRecords.add(Record(fields.map(::Cell), record.parent()))
        }

        return RecordSet(newRecords, newType)
    }

    fun select(vararg keys: String): RecordSet {
        return select(keys.toList())
    }

    fun where(key: String, assertion: String.() -> Boolean): RecordSet {
        checkKeyExists(key)
        val filtered = records.filter { rec ->
            accessor.of(rec)[key].bind(assertion).orElse { false }
        }
        return RecordSet(filtered, config)
    }

    fun where(assertion: (RecordWrapper<String>) -> Boolean): RecordSet {
        val filtered = records.filter { rec ->
            assertion.invoke(accessor.of(rec))
        }

        return RecordSet(filtered, config)
    }

    companion object {
        fun loadData(lines: List<String>, config: RecConfig): RecordSet {
            val parser = SimpleParser(config.parseConfig())
            val records = arrayListOf<Record>()
            for (line in lines) {
                val record = parser.parse(line)
                record.bind { records.add(toRecord(it)) }
            }

            return RecordSet(records, config)
        }
    }

    fun isUnique(): Boolean {
        return records.toSet().size == records.size
    }

    fun contains(key: String, value: String): Boolean {
        return indices[key].bind { it.containsKey(value) }.orElse { false }
    }


    operator fun get(name: String): RecordSet {
        return select(name)
    }

    private fun checkKeyExists(vararg keys: String) {
        assert(indices.keys.containsAll(keys.asList()))
    }

    private fun newType(keys: List<String>, providedName: String? = null): RecConfig {
        return object: RecConfig {
            override fun name() = providedName.orElse { "image of ${config.name()}" }
            override fun parseConfig() = config.parseConfig()
            override fun key() = config.key()
            override fun format() = keys.joinToString(config.parseConfig().delimiter.toString())
            override fun accessor() = Accessor<String>(keys.toTypedArray())
        }
    }

    fun verify(assertion: (RecordWrapper<String>) -> Boolean) {
        val unexpected = arrayListOf<Record>()
        records.forEach {
            if (!assertion.invoke(accessor.of(it)))
                unexpected.add(it)
        }

        unexpected.forEach {
            println("Unexpected record found [ ${it.text} ]")
        }
    }
}