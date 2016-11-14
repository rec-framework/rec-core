package net.kimleo.rec.repository.selector.expr

import net.kimleo.rec.repository.selector.FieldSelector
import org.junit.Assert.*
import org.junit.Test

class SelectorParserTest {
    @Test
    fun testParser() {
        val lex = SelectorParser.lex("cust, prod.world, i2b[name, pass], intj")
        val parse = SelectorParser.parse(lex)

        assertTrue(parse.size == 4)
        assertTrue((parse[1] as FieldSelector).properties.size == 1)
        assertTrue((parse[2] as FieldSelector).properties.size == 2)
    }
}