package net.kimleo.rec.repository

import net.kimleo.rec.repository.selector.expr.SelectorExprLexer
import org.junit.Test

class SelectorExprTest {
    @Test
    fun testLexer() {
        val result = SelectorExprLexer().lex("hello hello, hello.name, hello world[hahaha] | hello hello == 1, hello is 5")
        println(result)
    }
}