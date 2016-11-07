package net.kimleo.rec.repository

import net.kimleo.rec.record.builder.RecCollectBuilder
import net.kimleo.rec.record.builder.RecTypeBuilder
import net.kimleo.rec.rule.RuleLoader
import net.kimleo.rec.rule.impl.Unique
import org.junit.Assert.*
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader

class RecRepositoryTest {
    val records = lines("person_test.txt")
    val rec = lines("person_test.txt.rec")
    val rule = lines("default.rule")

    @Test
    fun testRepository() {

        val type = RecTypeBuilder().build(rec)

        val collect = RecCollectBuilder().build(records, type)

        val repo = RecRepository(listOf(collect))

        assertNotNull(repo)

        assertTrue(repo.from("Person").where("first name", "Kimmy").records.size == 1)

        val (unique1) = Unique().verify(listOf(collect.select("first name")))
        assertTrue(unique1)

        val (unique2, result) = Unique().verify(listOf(collect.select("comment")))

        assertFalse(unique2)
        assertTrue(result.size == 5)

        val names = repo.select("Person[first name], Person[comment]")

        assertTrue(names.size == 2)
        assertTrue(names[1].records.size == 5)

        val ruleResult = RuleLoader()
                .load(listOf("unique: Person[first name]", "unique: Person[comment]")).map {
            it.runOn(repo)
        }.toList()

        assert(ruleResult.size == 2)

        assert(ruleResult[1].second.size == 5)
    }

    private fun lines(file: String): List<String> {
        val stream = javaClass.classLoader.getResourceAsStream(file)
        val reader = BufferedReader(InputStreamReader(stream))

        val lines = reader.readLines()
        reader.close()
        return lines
    }
}