package net.kimleo.rec.repository.selector

import net.kimleo.rec.record.RecCollection
import net.kimleo.rec.repository.RecRepository
import java.util.*

class CombinedSelector(val selectors: List<Selector>) : Selector {
    override fun findAll(repo: RecRepository): List<RecCollection> {
        return selectors.map { it.findAll(repo) }.reduce { left, right ->
            left + right
        }
    }

}