package net.kimleo.rec.mapper.impl

import net.kimleo.rec.concept.Mapped
import net.kimleo.rec.mapper.BeanCustomer
import org.junit.Assert.assertEquals
import org.junit.Test

class AccessorMapperBuilderTest {

    data class Customer(val firstName: String, val lastName: String = "None")

    class FieldCustomer {
        var firstName: String? = null
        var lastName: String? = null
    }

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
    fun buildCustomer() {
        val map = createNewMap()
        val customer = builder.build(Customer::class).map(map)

        assertEquals(customer.firstName, "Kimmy")
        assertEquals(customer.lastName, "Leo")
    }

    @Test
    fun buildFieldCustomer() {
        val map = createNewMap()
        val customer = builder.build(FieldCustomer::class).map(map)

        assertEquals(customer.firstName, "Kimmy")
        assertEquals(customer.lastName, "Leo")
    }

    @Test
    fun buildBeanCustomer() {
        val map = createNewMap()
        val customer = builder.build(BeanCustomer::class).map(map)

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