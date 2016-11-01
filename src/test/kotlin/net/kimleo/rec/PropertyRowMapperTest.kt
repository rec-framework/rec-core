package net.kimleo.rec

import org.junit.Assert.*
import org.junit.Test

class PropertyRowMapperTest {
    class User {
        var name: String = ""
        var age: String = ""
    }
    @Test
    fun should_map_simple_data_class() {


        val mapper = PropertyRowMapper(User::class.java)

        val user = mapper.map(MappedTuple(Tuple(listOf(Field("name"), Field("age"))),
                Tuple(listOf(Field("KimmyLeo"), Field("18")))))

        assertEquals(user.name, "KimmyLeo")
        assertEquals(user.age, "18")
    }
}