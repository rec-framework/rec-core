package net.kimleo.rec.repository.selector.expr

import net.kimleo.rec.repository.selector.CombinedSelector
import net.kimleo.rec.repository.selector.Selector

object SelectorExpr {
    fun buildSelector(expr: String): Selector {
        val selectors = SelectorParser.parse(SelectorParser.lex(expr))
        return CombinedSelector(selectors)
    }
}