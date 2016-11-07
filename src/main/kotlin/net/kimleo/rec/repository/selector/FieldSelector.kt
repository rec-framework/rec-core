package net.kimleo.rec.repository.selector

import net.kimleo.rec.record.RecCollection
import net.kimleo.rec.repository.RecRepository

class FieldSelector(val type: String, val properties: List<String>) : Selector {
    override fun findAll(repo: RecRepository): List<RecCollection> {
        return listOf(repo.from(type).select(properties, "$type$properties"))
    }
}