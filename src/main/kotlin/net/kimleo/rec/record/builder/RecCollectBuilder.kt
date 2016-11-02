package net.kimleo.rec.record.builder

import net.kimleo.rec.bind
import net.kimleo.rec.record.Record
import net.kimleo.rec.record.RecCollection
import net.kimleo.rec.record.RecType
import net.kimleo.rec.record.parser.SimpleParser

class RecCollectBuilder {
    fun build(lines: List<String>, type: RecType): RecCollection {
        val parser = SimpleParser(type.parseConfig)
        val records = arrayListOf<Record>()
        for (line in lines) {
            val record = parser.parse(line)
            record.bind { records.add(it) }
        }

        return RecCollection(records, type)
    }
}