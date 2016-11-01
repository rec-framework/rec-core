package net.kimleo.rec

data class Field(val value: String) {
    operator fun plus(otherValue: String): Record {
        return Record(listOf(this, Field(otherValue)))
    }
}

data class Record(val fields: List<Field>) {

    val size = fields.size

    operator fun plus(value: String): Record {
        return Record(fields + Field(value))
    }
}

data class MappedTuple(val header: Record, val record: Record) {
    val values = hashMapOf<String, String>()

    init {
        assert(header.fields.size == record.fields.size)
        for (i in 0..header.fields.size - 1) {
            values.put(header.fields[i].value, record.fields[i].value)
        }
    }

    fun get(name: String): String? {
        return values.get(name)
    }
}

fun buildFieldMapPair(vararg fields: String): Pair<Map<String, Int>, Int> {
    val accessorMap = hashMapOf<String, Int>()
    val (accessors, leastCapacity) = lex(fields)
    val reversedAccessor = accessors.asReversed()
    var reversed = false
    var index = 0;
    for (accessor in accessors) {
        if (accessor is FieldName) {
            accessorMap.put(accessor.name, index)
            index ++
        } else if (accessor is Placeholder) {
            index += accessor.size
        } else if (accessor is Padding) {
            if (reversed) {
                throw UnsupportedOperationException()
            }
            break
        }
    }
    reversed = true
    index = -1
    for (accessor in reversedAccessor) {
        if (accessor is FieldName) {
            accessorMap.put(accessor.name, index)
            index --
        } else if (accessor is Placeholder) {
            index -= accessor.size
        } else if (accessor is Padding) {
            if (!reversed) {
                throw UnsupportedOperationException()
            }
            break
        }
    }
    return Pair(accessorMap, leastCapacity)
}

interface Accessor {}

data class Placeholder(val size: Int = 1): Accessor

data class FieldName(val name: String): Accessor

data class Padding(val name: String): Accessor

fun lex(fields: Array<out String>): Pair<List<Accessor>, Int> {
    val segmentSizes = arrayListOf<Int>()
    var currentSegmentSize = 0
    val accessors = arrayListOf<Accessor>()
    for (field in fields) {
        if (field.trim().startsWith("{")) {
            val field1 = Regex("\\{(\\d+)\\}").find(field)
            val toInt = field1!!.groupValues[1].toInt()
            currentSegmentSize += toInt
            accessors.add(Placeholder(toInt))
        } else if (field.trim() == "...") {
            accessors.add(Padding(field.trim()))
            segmentSizes += currentSegmentSize
            currentSegmentSize = 0
        } else {
            accessors.add(FieldName(field.trim()))
            currentSegmentSize++
        }
    }
    segmentSizes.add(currentSegmentSize)
    return Pair(accessors, segmentSizes.max().orElse { 1 })
}

class AccessorFactory(fields: Array<out String>) {
    val fieldMapPair = buildFieldMapPair(*fields)
    val fieldMap = fieldMapPair.first
    val leastCapacity = fieldMapPair.second

    fun create(record: Record): RecordAccessor {
        assert(record.size >= leastCapacity)

        return RecordAccessor(fieldMap, record)
    }
}

data class RecordAccessor(val fields: Map<String, Int>, val record: Record) {
    val size = fields.size

    fun get(field: String): String? {
        if (fields.containsKey(field)) {
            return getByIndex(fields[field]!!, record)
        }
        return null
    }

    private fun getByIndex(index: Int, record: Record): String? {
        if (index >= 0) {
            return record.fields[index].value
        } else {
            return record.fields[record.size + index].value
        }
    }
}

fun <T, U> T?.bind(fn: (T) -> U?): U? =
        if (this == null) null else fn.invoke(this)

fun <T> T?.orElse(fn: () -> T) =
        if (this != null) this else fn.invoke()