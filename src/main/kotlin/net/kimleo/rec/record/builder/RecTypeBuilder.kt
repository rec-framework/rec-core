package net.kimleo.rec.record.builder

import net.kimleo.rec.accessor.AccessorFactory
import net.kimleo.rec.orElse
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

        return object : RecType {
            override val name = name
            override val parseConfig = ParseConfig(
                    configs["delimiter"]?.get(0).orElse { ',' },
                    configs["escape"]?.get(0).orElse { '"' })
            override val key = configs["key"]
            override val format = configs["format"]!!
            override val accessor = AccessorFactory(SimpleParser().parse(format)!!)
        }
    }
}