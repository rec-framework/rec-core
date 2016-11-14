package net.kimleo.rec.record.builder

import net.kimleo.rec.accessor.Accessor
import net.kimleo.rec.orElse
import net.kimleo.rec.record.DefaultRecType
import net.kimleo.rec.record.RecType
import net.kimleo.rec.record.parser.ParseConfig
import net.kimleo.rec.record.parser.SimpleParser

class RecTypeBuilder {
    fun build(lines: List<String>): RecType {
        val name = lines.first().trim()
        val configs = hashMapOf<String, String>()
        lines.drop(1).forEach {
            val (key, value) = it.split("=")
            configs.put(key.trim(), value.trim())
        }

        val format = configs["format"]!!
        val config = ParseConfig(
                configs["delimiter"]?.get(0).orElse { ',' },
                configs["escape"]?.get(0).orElse { '"' })

        return DefaultRecType(name, format, config, configs["key"], Accessor(SimpleParser().parse(format)))
    }
}