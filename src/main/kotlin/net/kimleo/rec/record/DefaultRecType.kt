package net.kimleo.rec.record

import net.kimleo.rec.accessor.Accessor
import net.kimleo.rec.record.parser.ParseConfig
import net.kimleo.rec.record.parser.SimpleParser

class DefaultRecType(
        override var name: String,
        override var format: String,
        override var parseConfig: ParseConfig = ParseConfig(),
        override var key: String? = null,
        override var accessor: Accessor = Accessor(SimpleParser().parse(format))) : RecType {
    companion object {
        fun create(name: String, format: String): RecType {
            return DefaultRecType(name, format)
        }
    }
}