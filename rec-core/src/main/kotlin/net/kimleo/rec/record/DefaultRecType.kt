package net.kimleo.rec.record

import net.kimleo.rec.accessor.Accessor
import net.kimleo.rec.orElse
import net.kimleo.rec.sepval.parser.ParseConfig
import net.kimleo.rec.sepval.parser.SimpleParser

class DefaultRecType(
        override var name: String,
        override var format: String,
        override var parseConfig: ParseConfig = ParseConfig(),
        override var key: String? = null,
        override var accessor: Accessor<String> = Accessor(SimpleParser().parse(format).values.toTypedArray())) : RecType {
    companion object {
        fun create(name: String, format: String): RecType {
            return DefaultRecType(name, format)
        }

        fun makeTypeFrom(lines: List<String>): RecType {
            val name = lines.first().trim()
            val configs = hashMapOf<String, String>()
            lines.drop(1).forEach {
                val (key, value) = it.split("=")
                configs.put(key.trim(), value.trim())
            }

            val format = configs["format"]!!
            val config = ParseConfig(
                    configs["delimiter"]?.first().orElse { ',' },
                    configs["escape"]?.first().orElse { '"' })

            return DefaultRecType(name, format, config, configs["key"], Accessor(SimpleParser().parse(format).values.toTypedArray()))
        }
    }
}