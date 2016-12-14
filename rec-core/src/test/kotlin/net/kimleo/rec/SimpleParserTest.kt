package net.kimleo.rec

import net.kimleo.rec.sepval.parser.ParseConfig
import net.kimleo.rec.sepval.parser.SimpleParser
import org.junit.Assert.*
import org.junit.Test

class SimpleParserTest {
    val parser = SimpleParser()

    @Test
    fun should_parse_single_csv() {
        val tuple = parser.parse("a, b, c")

        assertEquals(tuple.values.size, 3)
        assertEquals(tuple[1], "b")
    }

    @Test
    fun should_parse_quoted_string() {
        val tuple = parser.parse("\"abc\",    \" \"\" \t\r\n\\\b def \"    , g")

        assertEquals(tuple.size, 3)
        assertEquals(tuple[1], " \" \t\r\n\\\b def ")
        assertEquals(tuple[2], "g")
    }

    @Test
    fun should_parse_different_delimiter() {
        val tuple = SimpleParser(ParseConfig('|')).parse("hello|world|    123456|\"\"\"I have a dream\"\"\"")

        assertEquals(tuple.size, 4)
        assertEquals(tuple[0], "hello")
        assertEquals(tuple[2], "123456")
        assertEquals(tuple[3], "\"I have a dream\"")
    }

    @Test
    fun should_parse_record_format() {
        val tuple = SimpleParser().parse("first name, last name, {2}, ID, ..., comment")

        assertNotNull(tuple)
    }
}