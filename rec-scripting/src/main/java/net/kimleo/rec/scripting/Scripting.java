package net.kimleo.rec.scripting;

import net.kimleo.rec.scripting.model.Rec;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class Scripting {

    public static void runfile(File file, String filename) throws Exception {
        Context ctx = Context.enter();
        Reader reader = new FileReader(file);
        ctx.putThreadLocal("SCRIPT_PATH", file.getAbsoluteFile().getParent());

        runReader(filename, ctx, reader);
    }

    public static void runReader(String filename, Context ctx, Reader reader) throws IllegalAccessException, InstantiationException, java.lang.reflect.InvocationTargetException, IOException {
        ScriptableObject scope = ctx.initStandardObjects();

        Object wrappedOut = Context.javaToJS(System.out, scope);
        ScriptableObject.defineClass(scope, Rec.class);
        ScriptableObject.putProperty(scope, "out", wrappedOut);
        ctx.evaluateReader(scope, reader, filename, 1, null);
    }
}
