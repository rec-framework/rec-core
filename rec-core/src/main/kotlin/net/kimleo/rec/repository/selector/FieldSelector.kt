package net.kimleo.rec.repository.selector

import net.kimleo.rec.loader.RecCollection
import net.kimleo.rec.repository.RecRepository

data class FieldSelector(val type: String, val properties: List<String>) : Selector {
    override fun findAll(repo: RecRepository): List<RecCollection> {
        return listOf(repo.from(type).select(properties, "$type$properties"))
    }
}