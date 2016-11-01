package net.kimleo.rec.record

import net.kimleo.rec.accessor.AccessorFactory
import net.kimleo.rec.record.parser.Configuration

interface RecordType {
    val name: String
    val configuration: Configuration
    val key: String?
    val format: String
    val accessor: AccessorFactory
}