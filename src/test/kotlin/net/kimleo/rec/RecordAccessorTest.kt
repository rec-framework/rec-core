package net.kimleo.rec

import org.junit.Test
import kotlin.test.assertEquals

class RecordAccessorTest {
    @Test
    fun shouldBuildAccessorMapSuccessfully() {
        val (map1, leastCapacity1) = buildFieldMapPair("name", "age", "...", "email", "{1}", "comment")

        assertEquals(map1["name"], 0)
        assertEquals(map1["comment"], -1)
        assertEquals(map1["email"], -3)

        assertEquals(leastCapacity1, 3)

        val (map2, leastCapacity2) = buildFieldMapPair("hello", "{3}", "world", "...", "is", "{12}", "beep", "{6}", "end")

        assertEquals(map2["hello"], 0)
        assertEquals(map2["world"], 4)
        assertEquals(map2["is"], -21)
        assertEquals(leastCapacity2, 21)
    }

    @Test
    fun shouldAccessCorrectRecord() {
        val record = Field("Kimmy") + "Leo" + "10" + "male" + "1993/07/09"

        val fact = AccessorFactory(arrayOf("first name", "...", "age", "{1}", "dob"))
        val kimmy = fact.create(record)

        assertEquals(kimmy.get("first name"), "Kimmy")
        assertEquals(kimmy.get("age"), "10")
        assertEquals(kimmy.get("dob"), "1993/07/09")
    }
}