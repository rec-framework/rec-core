package net.kimleo.rec.scripting;

import net.kimleo.rec.execution.impl.NativeExecutionContext;
import net.kimleo.rec.scripting.module.CoreRecPlugin;
import net.kimleo.rec.scripting.module.NativeModuleSourceProvider;
import net.kimleo.rec.scripting.module.Rec;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;

import java.io.File;

import static net.kimleo.rec.execution.impl.NativeExecutionContext.initialContext;
import static org.mozilla.javascript.Context.VERSION_1_8;
import static org.mozilla.javascript.Context.javaToJS;

public class Scripting {
    public static void runfile(File file,
                               String filename,
                               boolean enableRetry,
                               String retryFile) throws Exception {
        Context ctx = Context.enter();
        ctx.putThreadLocal("SCRIPT_PATH", file.getAbsoluteFile().getParent());

        ScriptableObject scope = ctx.initStandardObjects();

        NativeExecutionContext executionContext = initializeNativeExecutionContext(
                file, enableRetry, retryFile, ctx);

        scope.putConst("context", scope, javaToJS(executionContext, scope));

        // Rec: the single bridge class
        Rec rec = new Rec(ctx, scope, executionContext);
        scope.put("__rec", scope, rec);

        // CoreRecPlugin: csv/counter/collect/target/dummy/stream/stateless/unique
        CoreRecPlugin core = new CoreRecPlugin(ctx, rec);
        scope.put("__core", scope, core);

        // Discover and install feature plugins (jdbi, reactive, cache, ...)
        rec.installPlugins();

        ctx.setLanguageVersion(VERSION_1_8);
        Require require = new RequireBuilder()
                .setModuleScriptProvider(new SoftCachingModuleScriptProvider(new NativeModuleSourceProvider()))
                .createRequire(ctx, scope);
        require.install(scope);
        require.requireMain(ctx, filename);
    }

    private static NativeExecutionContext initializeNativeExecutionContext(File file,
                                                                           boolean enableRetry,
                                                                           String retryFile,
                                                                           Context ctx) {
        NativeExecutionContext executionContext = initialContext();
        executionContext.setScriptPath(file.getAbsoluteFile().getParent());
        executionContext.setJsContext(ctx);
        executionContext.setEnableRetry(enableRetry);
        executionContext.setRetryFile(retryFile);
        return executionContext;
    }
}
