package net.kimleo.rec.record

data class Record(val fields: List<Field>, val text: String) {

    val size = fields.size

    constructor(fields: List<Field>): this(fields, fields.map(Field::value).joinToString(", ")) {}

    operator fun plus(value: String): Record {
        return Record(fields + Field(value))
    }
}