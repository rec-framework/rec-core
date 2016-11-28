import net.kimleo.rec.api.map
import net.kimleo.rec.repository.DefaultRecType
import net.kimleo.rec.repository.RecCollection
import net.kimleo.rec.repository.RecRepository
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.test.assertTrue

class IntgTest {

    data class Person(val firstName: String, val lastName: String)

    @Test
    fun testIntegration() {
        val records = lines("person_test.txt")
        val rec = lines("person_test.txt.rec")

        val type = DefaultRecType.makeTypeFrom(rec)

        val collect = RecCollection.loadData(records, type)

        val repo = RecRepository(listOf(collect))

        val persons = collect.map(Person::class)

        assertTrue { persons.size == 5 }

        assertTrue { persons.first { it.firstName == "Kimmy" }.lastName == "Leo" }

        val persons2 = repo.map(Person::class)

        assertTrue { persons == persons2 }

    }

    private fun lines(file: String): List<String> {
        val stream = javaClass.classLoader.getResourceAsStream(file)
        val reader = BufferedReader(InputStreamReader(stream))

        val lines = reader.readLines()
        reader.close()
        return lines
    }
}