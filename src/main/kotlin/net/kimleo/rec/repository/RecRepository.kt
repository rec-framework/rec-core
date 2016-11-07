package net.kimleo.rec.repository

import net.kimleo.rec.record.RecCollection
import net.kimleo.rec.record.RecType
import net.kimleo.rec.repository.selector.expr.SelectorExpr
import java.util.*

class RecRepository(val collections: List<RecCollection>) {
    val repo: Map<String, RecCollection> = toRepoMap(collections)

    fun from(name: String): RecCollection = repo.getOrElse(name) {
        throw NoSuchElementException("Cannot found the collection $name.")
    }

    private fun toRepoMap(collections: List<RecCollection>): Map<String, RecCollection> {
        return collections
                .map(RecCollection::type)
                .map(RecType::name)
                .zip(collections).toMap()
    }

    fun select(expr: String): List<RecCollection> {
        return SelectorExpr().buildSelector(expr).findAll(this)
    }
}

