package net.kimleo.rec.v2.scripting;

import net.kimleo.rec.v2.scripting.model.Rec;
import net.kimleo.rec.v2.scripting.module.RecModuleSourceProvider;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.MultiModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;

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

    public static void runReader(String filename, Context ctx, Reader reader) throws Exception {
        ScriptableObject scope = ctx.initStandardObjects();
        ctx.setLanguageVersion(VERSION_1_8);
        Require require = new RequireBuilder()
                .setModuleScriptProvider(new SoftCachingModuleScriptProvider(new RecModuleSourceProvider())).createRequire(ctx, scope);
        require.install(scope);

        Object rec = Context.javaToJS(new Rec(ctx, scope), scope);
        ScriptableObject.putConstProperty(scope, "rec", rec);

        ctx.evaluateReader(scope, reader, filename, 1, null);
    }
}
