package net.kimleo.rec.repository.selector.expr

import net.kimleo.rec.repository.selector.FieldSelector
import net.kimleo.rec.repository.selector.expr.SelectorParser.lex
import net.kimleo.rec.repository.selector.expr.SelectorParser.parse
import org.junit.Assert.*
import org.junit.Test

class SelectorParserTest {
    @Test
    fun testParser() {
        val lex = lex("cust, prod.hello world, i2b[name, pass], intj")
        val parse = parse(lex)

        assertTrue(parse.size == 4)
        assertTrue((parse[1] as FieldSelector).properties.size == 1)
        assertTrue((parse[1] as FieldSelector).properties[0] == "hello world")
        assertTrue((parse[2] as FieldSelector).properties.size == 2)
    }
}