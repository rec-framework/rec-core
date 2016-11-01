package net.kimleo.rec.record

import net.kimleo.rec.bind
import net.kimleo.rec.record.parser.SimpleParser

class RecordCollectionBuilder {
    fun build(lines: List<String>, type: RecordType): RecordCollection {
        val parser = SimpleParser(type.configuration)
        val records = arrayListOf<Record>()
        for (line in lines) {
            val record = parser.parse(line)
            record.bind { records.add(it) }
        }

        return RecordCollection(records, type)
    }
}