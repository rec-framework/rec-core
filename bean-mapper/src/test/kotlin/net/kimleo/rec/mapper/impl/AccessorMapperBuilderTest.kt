package net.kimleo.rec.mapper.impl

import net.kimleo.rec.concept.Mapped
import net.kimleo.rec.mapper.impl.AccessorMapperBuilder.Companion.toStandardJavaName
import org.junit.Test

import org.junit.Assert.*
import org.junit.Ignore

class AccessorMapperBuilderTest {

    data class Customer(val firstName: String, val lastName: String)

    val builder = AccessorMapperBuilder()

    private fun createNewMap() = object : Mapped<String> {
        val map = hashMapOf<String, String>()

        init {
            map["first name"] = "Kimmy"
            map["Last Name"] = "Leo"
        }

        override fun get(field: String): String {
            return map[field]!!
        }

        override fun keys(): List<String> {
            return map.keys.toList()
        }
    }

    @Test
    fun build() {
        val customer = builder.build(Customer::class).map(createNewMap())

        assertEquals(customer.firstName, "Kimmy")
        assertEquals(customer.lastName, "Leo")
    }

    @Test
    fun testToJavaStandardName() {
        assertEquals("hello world".javaName(), "helloWorld")
        assertEquals("shit".javaName(), "shit")
        assertEquals("hello WORLD you".javaName(), "helloWORLDYou")
    }
}