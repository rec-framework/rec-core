package net.kimleo.rec.repository

import net.kimleo.rec.accessor.Accessor
import net.kimleo.rec.accessor.RecordWrapper
import net.kimleo.rec.bind
import net.kimleo.rec.concept.QuerySelector
import net.kimleo.rec.concept.Queryable
import net.kimleo.rec.orElse
import net.kimleo.rec.record.Cell
import net.kimleo.rec.record.Record
import net.kimleo.rec.record.toRecord
import net.kimleo.rec.sepval.parser.SimpleParser

class RecordSet(val records: List<Record>, val config: RecConfig): Iterable<Record>, Queryable<Record> {
    override fun where(selector: QuerySelector<Record>, fn: Record.() -> Boolean): RecordSet {
        throw NotImplementedError()
    }

    override fun iterator(): Iterator<Record> {
        return records.iterator()
    }

    val accessor = config.accessor

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
            val parser = SimpleParser(config.parseConfig)
            val records = arrayListOf<Record>()
            for (line in lines) {
                val record = parser.parse(line)
                record.bind { records.add(it.toRecord()) }
            }

            return RecordSet(records, config)
        }
    }

    fun isUnique(): Boolean {
        return records.toSet().size == records.size
    }


    operator fun get(name: String): RecordSet {
        return select(name)
    }

    private fun checkKeyExists(vararg keys: String) {
        for (key in keys) {
            assert(key in accessor.fieldMap.keys)
        }
    }

    private fun newType(keys: List<String>, providedName: String? = null): RecConfig {
        return object: RecConfig {
            override val name = providedName.orElse { "image of ${config.name}" }
            override val parseConfig = config.parseConfig
            override val key = config.key
            override val format = keys.joinToString(config.parseConfig.delimiter.toString())
            override val accessor = Accessor<String>(keys.toTypedArray())
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