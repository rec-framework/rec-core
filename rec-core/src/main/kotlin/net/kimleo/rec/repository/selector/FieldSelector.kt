package net.kimleo.rec.repository.selector

import net.kimleo.rec.repository.RecordSet
import net.kimleo.rec.repository.RecRepository

data class FieldSelector(val type: String, val properties: List<String>) : Selector {
    var newType = type;
    override fun findAll(repo: RecRepository): List<RecordSet> {
        return listOf(repo.from(type).select(properties, newType))
    }
}