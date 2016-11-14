package net.kimleo.rec.record

import net.kimleo.rec.accessor.Accessor
import net.kimleo.rec.record.parser.ParseConfig

interface RecType {
    val name: String
    val parseConfig: ParseConfig
    val key: String?
    val format: String
    val accessor: Accessor
}