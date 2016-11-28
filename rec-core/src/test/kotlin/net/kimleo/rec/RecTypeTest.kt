package net.kimleo.rec

import net.kimleo.rec.repository.DefaultRecType
import net.kimleo.rec.repository.RecCollection
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class RecTypeTest {
    @Test
    fun shouldReturnType() {
        val lines = linesOfRes("person_test.txt.rec")

        val type = DefaultRecType.makeTypeFrom(lines)

        assertNotNull(type)
    }

    @Test
    fun shouldParseRecord() {
        val records = linesOfRes("person_test.txt")
        val rec = linesOfRes("person_test.txt.rec")

        val type = DefaultRecType.makeTypeFrom(rec)

        val collect = RecCollection.loadData(records, type)

        assertNotNull(collect)

        assertEquals(collect.where("first name"){contains("Kim")}.records.size, 3)
        assertEquals(collect.where("first name"){contains("Kimm")}.records.size, 1)

    }

}