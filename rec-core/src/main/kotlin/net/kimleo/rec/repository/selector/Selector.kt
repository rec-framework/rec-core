package net.kimleo.rec.repository.selector

import net.kimleo.rec.repository.RecCollection
import net.kimleo.rec.repository.RecRepository
import net.kimleo.rec.repository.selector.expr.SelectorParser

interface Selector {
    fun findAll(repo: RecRepository): List<RecCollection>

    companion object {
        fun of(expr: String): Selector {
            val selectors = SelectorParser.parse(SelectorParser.lex(expr))
            return CombinedSelector(selectors)
        }
    }
}