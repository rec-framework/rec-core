package net.kimleo.rec.repository

import net.kimleo.rec.repository.selector.expr.SelectorParser
import org.junit.Test

class SelectorExprTest {
    @Test
    fun testLexer() {
        val result = SelectorParser.lex("hello hello, hello.name, hello world[hahaha] as fuck, hello")
        println(result)
    }
}