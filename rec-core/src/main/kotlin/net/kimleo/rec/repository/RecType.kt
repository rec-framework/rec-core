package net.kimleo.rec.repository

import net.kimleo.rec.accessor.Accessor
import net.kimleo.rec.sepval.parser.ParseConfig

interface RecType {
    val name: String
    val parseConfig: ParseConfig
    val key: String?
    val format: String
    val accessor: Accessor<String>
}