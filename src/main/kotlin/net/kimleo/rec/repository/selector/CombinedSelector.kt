package net.kimleo.rec.repository.selector

import net.kimleo.rec.record.RecCollection
import net.kimleo.rec.repository.RecRepository

class CombinedSelector(val selectors: List<Selector>) : Selector {
    override fun findAll(repo: RecRepository): List<RecCollection> {
        return selectors.map { it.findAll(repo) }.reduce { left, right ->
            left + right
        }
    }
}