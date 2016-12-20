package net.kimleo.rec.repository

import net.kimleo.rec.concept.QuerySelector
import net.kimleo.rec.concept.Queryable
import net.kimleo.rec.repository.selector.Selector
import java.util.*
import java.util.function.Function

class RecRepository(val collections: List<RecordSet>): Iterable<RecordSet> {

    override fun iterator(): Iterator<RecordSet> {
        return collections.iterator()
    }

    val repo: Map<String, RecordSet> = toRepoMap(collections)
    val size = collections.size

    fun from(name: String): RecordSet = repo.getOrElse(name) {
        throw NoSuchElementException("Cannot found the collection $name.")
    }

    private fun toRepoMap(collections: List<RecordSet>): Map<String, RecordSet> {
        return collections
                .map(RecordSet::config)
                .map(RecConfig::name)
                .zip(collections).toMap()
    }

    fun select(expr: String): RecRepository {
        return RecRepository(Selector.of(expr).findAll(this))
    }

    operator fun get(expr: String): RecRepository {
        return select(expr)
    }

    operator fun get(index: Int): RecordSet {
        return collections[index]
    }
}

