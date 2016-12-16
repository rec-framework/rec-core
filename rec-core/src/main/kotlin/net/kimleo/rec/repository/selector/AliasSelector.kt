package net.kimleo.rec.repository.selector

import net.kimleo.rec.repository.RecRepository
import net.kimleo.rec.repository.RecordSet

class AliasSelector(val selector: FieldSelector, val alias: String): Selector {
    override fun findAll(repo: RecRepository): List<RecordSet> {
        selector.newType = alias
        return selector.findAll(repo)
    }

}