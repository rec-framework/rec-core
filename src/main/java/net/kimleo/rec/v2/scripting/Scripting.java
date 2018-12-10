package net.kimleo.rec.v2.scripting;

import net.kimleo.rec.v2.execution.impl.NativeExecutionContext;
import net.kimleo.rec.v2.scripting.module.NativeModuleSourceProvider;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;

import java.io.File;

import static net.kimleo.rec.v2.execution.impl.NativeExecutionContext.initialContext;
import static org.mozilla.javascript.Context.VERSION_1_8;
import static org.mozilla.javascript.Context.javaToJS;

@Deprecated
public class Scripting {
    @Deprecated
    public static void runfile(File file,
                               String filename,
                               boolean enableRetry,
                               String retryFile) throws Exception {
        Context ctx = Context.enter();
        ctx.putThreadLocal("SCRIPT_PATH", file.getAbsoluteFile().getParent());

        ScriptableObject scope = ctx.initStandardObjects();

        NativeExecutionContext executionContext = initializeNativeExecutionContext(
                file,
                enableRetry,
                retryFile,
                ctx);

        scope.putConst("context", scope, javaToJS(executionContext, scope));

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
