import net.kimleo.rec.api.map
import net.kimleo.rec.linesOfRes
import net.kimleo.rec.repository.DefaultRecConfig
import net.kimleo.rec.repository.RecordSet
import net.kimleo.rec.repository.RecRepository
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.test.assertTrue

class IntgTest {

    data class Person(val firstName: String, val lastName: String)

    @Test
    fun testIntegration() {
        val records = linesOfRes("person_test.txt")
        val rec = linesOfRes("person_test.txt.rec")

        val type = DefaultRecConfig.makeTypeFrom(rec)

        val collect = RecordSet.loadData(records, type)

        val repo = RecRepository(listOf(collect))

        val persons = collect.map(Person::class)

        assertTrue { persons.size == 5 }

        assertTrue { persons.first { it.firstName == "Kimmy" }.lastName == "Leo" }

        val persons2 = repo.map(Person::class)

        assertTrue { persons == persons2 }

    }
}