package net.kimleo.rec.repository.selector

import net.kimleo.rec.record.RecCollection
import net.kimleo.rec.repository.RecRepository
import java.util.*

class MultipleCollectionSelector(val fieldMap: HashMap<String, List<String>>) : Selector {
    override fun findAll(repo: RecRepository): List<RecCollection> {
        val list = arrayListOf<RecCollection>()
        for ((type, fields) in fieldMap) {
            list += repo.from(type).select(*fields.toTypedArray())
        }

        return list
    }

}