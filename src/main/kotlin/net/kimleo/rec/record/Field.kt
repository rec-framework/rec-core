package net.kimleo.rec.record

data class Field(val value: String) {
    operator fun plus(otherValue: String): Record {
        return Record(listOf(this, Field(otherValue)))
    }
}