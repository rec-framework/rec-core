package net.kimleo.rec

import org.junit.Assert.*
import org.junit.Test

class SimpleParserTest {
    val parser = SimpleParser()

    @Test
    fun should_parse_single_csv() {
        val tuple = parser.parse("a, b, c")!!

        assertEquals(tuple.fields.size, 3)
        assertEquals(tuple.fields[1].value, "b")
    }

    @Test
    fun should_parse_quoted_string() {
        val tuple = parser.parse("\"abc\",    \" \"\" \t\r\n\\\b def \"    , g")!!

        assertEquals(tuple.fields.size, 3)
        assertEquals(tuple.fields[1].value, " \" \t\r\n\\\b def ")
        assertEquals(tuple.fields[2].value, "g")
    }

    @Test
    fun should_parse_different_delimiter() {
        val tuple = SimpleParser(Configuration('|')).parse("hello|world|    123456|\"\"\"I have a dream\"\"\"")!!

        assertEquals(tuple.fields.size, 4)
        assertEquals(tuple.fields[0].value, "hello")
        assertEquals(tuple.fields[2].value, "123456")
        assertEquals(tuple.fields[3].value, "\"I have a dream\"")
    }
}