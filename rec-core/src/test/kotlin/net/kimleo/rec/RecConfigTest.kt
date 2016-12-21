package net.kimleo.rec

import net.kimleo.rec.repository.DefaultRecConfig
import net.kimleo.rec.repository.RecordSet
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class RecConfigTest {

    fun linesOfRes(file: String): List<String> {
        val stream = RecConfigTest::class.java.classLoader.getResourceAsStream(file)
        val reader = BufferedReader(InputStreamReader(stream))

        val lines = reader.readLines()
        reader.close()
        return lines
    }

    @Test
    fun shouldReturnType() {
        val lines = linesOfRes("person_test.txt.rec")

        val type = DefaultRecConfig.makeTypeFrom(lines)

        assertNotNull(type)
    }

    @Test
    fun shouldParseRecord() {
        val records = linesOfRes("person_test.txt")
        val rec = linesOfRes("person_test.txt.rec")

        val type = DefaultRecConfig.makeTypeFrom(rec)

        val collect = RecordSet.loadData(records.stream(), type)

        assertNotNull(collect)

        assertEquals(collect.where("first name"){it.contains("Kim")}.records.count(), 3)
        assertEquals(collect.where("first name"){it.contains("Kimm")}.records.count(), 1)

    }

}