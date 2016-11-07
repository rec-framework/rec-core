package net.kimleo.rec.repository

import net.kimleo.rec.repository.selector.expr.SelectorExpr
import net.kimleo.rec.repository.selector.expr.SelectorExprLexer
import org.junit.Assert.*
import org.junit.Test

class SelectorExprTest {
    @Test
    fun testLexer() {
        val sel = SelectorExpr("hello hello, hello.name, hello world[hahaha] | hello hello == 1, hello is 5")

        val result = SelectorExprLexer().lex(sel.expr)
        println(result)
    }
}