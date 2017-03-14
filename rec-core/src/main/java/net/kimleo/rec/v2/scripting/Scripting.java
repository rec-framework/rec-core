package net.kimleo.rec.v2.scripting;

import net.kimleo.rec.v2.scripting.model.Rec;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static org.mozilla.javascript.Context.VERSION_1_8;

public class Scripting {
    public static void runfile(File file, String filename) throws Exception {
        Context ctx = Context.enter();
        Reader reader = new FileReader(file);
        ctx.putThreadLocal("SCRIPT_PATH", file.getAbsoluteFile().getParent());

        runReader(filename, ctx, reader);
    }

    public static void runReader(String filename, Context ctx, Reader reader) throws IllegalAccessException, InstantiationException, java.lang.reflect.InvocationTargetException, IOException {
        ScriptableObject scope = ctx.initStandardObjects();
        ctx.setLanguageVersion(VERSION_1_8);

        Object rec = Context.javaToJS(new Rec(ctx, scope), scope);
        ScriptableObject.putConstProperty(scope, "rec", rec);

        ctx.evaluateReader(scope, reader, filename, 1, null);
    }
}
