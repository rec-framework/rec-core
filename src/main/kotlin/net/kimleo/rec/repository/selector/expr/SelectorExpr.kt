package net.kimleo.rec.repository.selector.expr

import net.kimleo.rec.repository.selector.MultipleCollectionSelector
import net.kimleo.rec.repository.selector.Selector

class SelectorExpr(val expr: String) {
    fun buildSelector(): Selector {
        val selectors = parseSelectorExpr(expr)
        return combine(selectors)
    }

    private fun parseSelectorExpr(expr: String): List<Selector> {
        val tokens = SelectorExprLexer().lex(expr)
        return SelectorExprParser().parse(tokens)
    }


    private fun combine(selectors: List<Selector>): Selector {
        val fieldMap = hashMapOf<String, List<String>>()

        return MultipleCollectionSelector(fieldMap)
    }

}