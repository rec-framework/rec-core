package net.kimleo.rec

import net.kimleo.rec.record.parser.SimpleParser
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.test.assertNotNull


class RecordTypeTest {
    @Test
    fun shouldReturnType() {
        val lines = lines("rec_test.txt.rec")

        val type = RecordTypeBuilder().build(lines)

        assertNotNull(type)
    }

    @Test
    fun shouldParseRecord() {
        val records = lines("rec_test.txt")
        val rec = lines("rec_test.txt.rec")

        val type = RecordTypeBuilder().build(rec)

    }

    private fun lines(file: String): List<String> {
        val stream = javaClass.classLoader.getResourceAsStream(file)
        val reader = BufferedReader(InputStreamReader(stream))

        val lines = reader.readLines()
        reader.close()
        return lines
    }
}