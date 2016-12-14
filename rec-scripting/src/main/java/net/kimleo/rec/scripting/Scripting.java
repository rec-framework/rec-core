package net.kimleo.rec.scripting;

import net.kimleo.rec.scripting.model.Rec;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import java.io.Reader;

public class Scripting {

    public static void runjs(Reader reader, String filename) throws Exception {
        Context ctx = Context.enter();

        ScriptableObject scope = ctx.initStandardObjects();

        Object wrappedOut = Context.javaToJS(System.out, scope);
        ScriptableObject.defineClass(scope, Rec.class);
        ScriptableObject.putProperty(scope, "out", wrappedOut);
        ctx.evaluateReader(scope, reader, filename, 1, null);
    }
}
