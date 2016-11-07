package net.kimleo.rec.repository

import net.kimleo.rec.record.builder.RecCollectBuilder
import net.kimleo.rec.record.builder.RecTypeBuilder
import net.kimleo.rec.rule.impl.Unique
import org.junit.Assert.*
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader

class RecRepositoryTest {
    @Test
    fun testRepository() {
        val records = lines("rec_test.txt")
        val rec = lines("rec_test.txt.rec")

        val type = RecTypeBuilder().build(rec)

        val collect = RecCollectBuilder().build(records, type)

        val repo = RecRepository(listOf(collect))

        assertNotNull(repo)

        assertTrue(repo.from("Record").where("first name", "Kimmy").records.size == 1)

        val (unique1) = Unique().verify(collect.select("first name"))
        assertTrue(unique1)

        val (unique2, result) = Unique().verify(collect.select("comment"))

        assertFalse(unique2)
        assertTrue(result.size == 5)
    }
    private fun lines(file: String): List<String> {
        val stream = javaClass.classLoader.getResourceAsStream(file)
        val reader = BufferedReader(InputStreamReader(stream))

        val lines = reader.readLines()
        reader.close()
        return lines
    }
}