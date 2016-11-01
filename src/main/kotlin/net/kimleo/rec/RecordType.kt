package net.kimleo.rec

import net.kimleo.rec.accessor.AccessorFactory
import net.kimleo.rec.record.parser.Configuration

interface RecordType {
    val name: String
    val configuration: Configuration
    val key: String?
    val format: String
    val accessor: AccessorFactory
}