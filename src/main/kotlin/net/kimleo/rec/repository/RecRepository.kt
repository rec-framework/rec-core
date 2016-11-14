package net.kimleo.rec.repository

import net.kimleo.rec.record.RecCollection
import net.kimleo.rec.record.RecType
import net.kimleo.rec.repository.selector.Selector
import net.kimleo.rec.repository.selector.expr.SelectorExpr
import java.util.*

class RecRepository(val collections: List<RecCollection>): Iterable<RecCollection> {
    override fun iterator(): Iterator<RecCollection> {
        return collections.iterator()
    }

    val repo: Map<String, RecCollection> = toRepoMap(collections)
    val size = collections.size

    fun from(name: String): RecCollection = repo.getOrElse(name) {
        throw NoSuchElementException("Cannot found the collection $name.")
    }

    private fun toRepoMap(collections: List<RecCollection>): Map<String, RecCollection> {
        return collections
                .map(RecCollection::type)
                .map(RecType::name)
                .zip(collections).toMap()
    }

    fun select(expr: String): RecRepository {
        return RecRepository(Selector.of(expr).findAll(this))
    }

    operator fun get(expr: String): RecRepository {
        return select(expr)
    }

    operator fun get(index: Int): RecCollection {
        return collections[index]
    }
}

