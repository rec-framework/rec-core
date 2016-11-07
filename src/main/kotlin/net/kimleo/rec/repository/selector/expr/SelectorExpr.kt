package net.kimleo.rec.repository.selector.expr

import net.kimleo.rec.repository.selector.CombinedSelector
import net.kimleo.rec.repository.selector.Selector

class SelectorExpr {
    fun buildSelector(expr: String): Selector {
        val selectors = parseSelectorExpr(expr)
        return combine(selectors)
    }

    private fun parseSelectorExpr(expr: String): List<Selector> {
        val tokens = SelectorExprLexer().lex(expr)
        return SelectorExprParser().parse(tokens)
    }


    private fun combine(selectors: List<Selector>): Selector {
        return CombinedSelector(selectors)
    }

}