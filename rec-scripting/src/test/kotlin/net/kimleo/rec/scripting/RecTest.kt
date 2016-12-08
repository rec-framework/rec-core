package net.kimleo.rec.scripting

import org.junit.Test
import org.mozilla.javascript.Context
import org.mozilla.javascript.ScriptableObject

class RecTest {
    @Test
    fun runResource() {
        val resources = RecTest::class.java.classLoader.getResourceAsStream("test.js")

        val ctx = Context.enter()
        val scope = ctx.initStandardObjects()
        val wrappedOut = Context.javaToJS(System.out, scope)
        ScriptableObject.defineClass(scope, Rec::class.java)
        ScriptableObject.putProperty(scope, "out", wrappedOut)
        ctx.evaluateReader(scope, resources.reader(), "test.js", 1, null)
    }
}
