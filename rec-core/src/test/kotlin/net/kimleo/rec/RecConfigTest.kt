package net.kimleo.rec

import net.kimleo.rec.repository.DefaultRecConfig
import net.kimleo.rec.repository.RecordSet
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class RecConfigTest {
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

        val collect = RecordSet.loadData(records, type)

        assertNotNull(collect)

        assertEquals(collect.where("first name"){contains("Kim")}.records.size, 3)
        assertEquals(collect.where("first name"){contains("Kimm")}.records.size, 1)

    }

}