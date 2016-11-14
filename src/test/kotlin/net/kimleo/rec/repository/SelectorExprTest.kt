package net.kimleo.rec.repository

import net.kimleo.rec.repository.selector.expr.SelectorParser
import org.junit.Test

class SelectorExprTest {
    @Test
    fun testLexer() {
        val result = SelectorParser.lex("hello hello, hello.name, hello world[hahaha] | hello hello == 1, hello is 5")
        println(result)
    }
}