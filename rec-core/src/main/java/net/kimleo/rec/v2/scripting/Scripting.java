package net.kimleo.rec.v2.scripting;

import net.kimleo.rec.v2.scripting.module.Rec;
import net.kimleo.rec.v2.scripting.module.RecModuleSourceProvider;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;

import java.io.File;

import static org.mozilla.javascript.Context.VERSION_1_8;

public class Scripting {
    public static void runfile(File file, String filename) throws Exception {
        Context ctx = Context.enter();
        ctx.putThreadLocal("SCRIPT_PATH", file.getAbsoluteFile().getParent());

        runReader(filename, ctx);
    }

    public static void runReader(String filename, Context ctx) throws Exception {
        ScriptableObject scope = ctx.initStandardObjects();
        ctx.setLanguageVersion(VERSION_1_8);
        Rec.initializeContext(ctx);

        Require require = new RequireBuilder()
                .setModuleScriptProvider(new SoftCachingModuleScriptProvider(new RecModuleSourceProvider()))
                .createRequire(ctx, scope);
        require.install(scope);
        require.requireMain(ctx, filename);
    }
}
