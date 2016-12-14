package net.kimleo.rec.scripting

import org.junit.Test
import org.mozilla.javascript.Context
import org.mozilla.javascript.ScriptableObject
import java.nio.charset.Charset

class RecTest {
    @Test
    fun runResource() {
        val resources = RecTest::class.java.classLoader.getResourceAsStream("test_intg.js")
        runjs(resources.reader(Charset.defaultCharset()), "<rec>")
    }
}
