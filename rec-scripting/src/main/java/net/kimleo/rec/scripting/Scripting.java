package net.kimleo.rec.scripting;

import net.kimleo.rec.scripting.model.*;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;

public class Scripting {
    /*


fun runjs(reader: Reader, filename: String = "<rec>") {
    val ctx = Context.enter()
    val scope = ctx.initStandardObjects()
    val wrappedOut = Context.javaToJS(System.out, scope)
    ScriptableObject.defineClass(scope, Rec::class.java)
    ScriptableObject.putProperty(scope, "out", wrappedOut)
    ctx.evaluateReader(scope, reader, filename, 1, null)
}
    * */

    public static void runjs(Reader reader, String filename) throws Exception {
        Context ctx = Context.enter();

        ScriptableObject scope = ctx.initStandardObjects();

        Object wrappedOut = Context.javaToJS(System.out, scope);
        ScriptableObject.defineClass(scope, Rec.class);
        ScriptableObject.putProperty(scope, "out", wrappedOut);
        ctx.evaluateReader(scope, reader, filename, 1, null);
    }
}
