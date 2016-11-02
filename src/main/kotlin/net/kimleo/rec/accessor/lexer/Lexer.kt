package net.kimleo.rec.accessor.lexer

import net.kimleo.rec.orElse

object Lexer {

    fun buildFieldMapPair(vararg fields: String): Pair<Map<String, Int>, Int> {
        val accessorMap = hashMapOf<String, Int>()
        val (accessors, leastCapacity) = lex(fields)
        val reversedAccessor = accessors.asReversed()
        var reversed = false
        var index = 0
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

    fun lex(fields: Array<out String>): Pair<List<FieldType>, Int> {
        val segmentSizes = arrayListOf<Int>()
        var currentSegmentSize = 0
        val accessors = arrayListOf<FieldType>()
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
}