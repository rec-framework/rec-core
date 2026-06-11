package net.kimleo.rec.scripting.module;

import net.kimleo.rec.cache.CachePlugin;
import net.kimleo.rec.common.exception.ResourceAccessException;
import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.execution.impl.CountBasedRestartableSource;
import net.kimleo.rec.execution.impl.NativeExecutionContext;
import net.kimleo.rec.model.Source;
import net.kimleo.rec.model.Target;
import net.kimleo.rec.model.Tee;
import net.kimleo.rec.model.impl.FlatFileTarget;
import net.kimleo.rec.plugin.RecPlugin;
import net.kimleo.rec.scripting.model.JSRecord;
import net.kimleo.rec.scripting.model.State;
import net.kimleo.rec.utils.Persistence;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.lang.String.format;

/**
 * The single bridge class. Provides:
 * <ul>
 *   <li>Rhino↔Java utilities: action, pred, wrap, stateful</li>
 *   <li>Native methods: file, flat, cache, restartable, println</li>
 *   <li>Plugin discovery: installs RecPlugin modules into the JS scope</li>
 * </ul>
 */
public class Rec {
    private static final Logger LOGGER = LoggerFactory.getLogger(Rec.class);

    private final Context cx;
    private final ScriptableObject scope;
    private NativeExecutionContext executionContext;
    private final String scriptPath;
    private CachePlugin cachePlugin;

    public Rec(Context cx, ScriptableObject scope, NativeExecutionContext executionContext) {
        this.cx = cx;
        this.scope = scope;
        this.executionContext = executionContext;
        this.scriptPath = executionContext.scriptPath();
        LOGGER.info("Initialized Rec context at " + scriptPath);

        ServiceLoader<CachePlugin> cachePlugins = ServiceLoader.load(CachePlugin.class);
        for (CachePlugin plugin : cachePlugins) {
            LOGGER.info("Loading cache plugin: {}", plugin.getClass().getName());
            this.cachePlugin = plugin;
            break;
        }
    }

    // ---- Plugin discovery ----

    public void installPlugins() {
        ServiceLoader<RecPlugin> plugins = ServiceLoader.load(RecPlugin.class);
        for (RecPlugin plugin : plugins) {
            LOGGER.info("Installing plugin: {}", plugin.name());
            Object module = Context.javaToJS(plugin.module(), scope);
            scope.put("__" + plugin.name(), scope, module);
        }
    }

    // ---- Bridge utilities (used by native.js) ----

    public Consumer<Object> action(Function function) {
        LOGGER.info("Action wrapper created for Function #{}", function.hashCode());
        return (object) ->
                function.call(cx, function.getParentScope(), null,
                        new Object[]{ wrapObject(object) });
    }

    public Predicate<Object> pred(Function function) {
        LOGGER.info("Predicate wrapper created for Function #{}", function.hashCode());
        return (func) ->
                (boolean) function.call(cx, function.getParentScope(), null,
                        new Object[]{ wrapObject(func) });
    }

    public static Object wrapObject(Object obj) {
        if (obj instanceof DataSet) {
            return new JSRecord((DataSet) obj);
        }
        return obj;
    }

    public Tee stateful(Scriptable initial, Function function) {
        State state = new State(initial);
        return new StatefulTee(state, function);
    }

    // ---- Native methods ----

    public Reader file(String filename) {
        try {
            return Files.newBufferedReader(Paths.get(scriptPath, filename));
        } catch (IOException e) {
            LOGGER.error("File {} cannot be found", filename);
            throw new ResourceAccessException("File cannot be found", e);
        }
    }

    public Tee cache(int size) {
        if (cachePlugin != null) {
            return cachePlugin.cache(size);
        }
        throw new UnsupportedOperationException("cache() requires rec-cache module on the classpath");
    }

    public Target flat(String filename) {
        LOGGER.info("FlatFileTarget created under name: #[{}]", filename);
        return new FlatFileTarget(Paths.get(scriptPath, filename).toFile());
    }

    public Source restartable(Source source) {
        if (executionContext.enableRetry()) {
            try {
                loadRetryContext(executionContext.retryFile());
            } catch (Exception e) {
                throw new ResourceAccessException("Unable to load restry context", e);
            }
        }
        return new CountBasedRestartableSource(source, executionContext);
    }

    public void println(Object... objs) {
        for (Object obj : objs) {
            System.out.println(obj);
        }
    }

    private void loadRetryContext(String retryfile) throws IOException, ClassNotFoundException {
        LOGGER.info("Loading execution context from {}", retryfile);
        if (retryfile != null && !retryfile.isEmpty()) {
            NativeExecutionContext loaded =
                    (NativeExecutionContext) Persistence.loadObjectFromFile(retryfile);
            loaded.setJsContext(cx);
            loaded.setScriptPath(scriptPath);
            this.executionContext = (NativeExecutionContext) loaded.restart();
            LOGGER.info("Loaded execution context {}", this.executionContext);
        }
    }

    // ---- Stateful tee ----

    public class StatefulTee implements Tee {
        private final State state;
        private final Function function;

        StatefulTee(State state, Function function) {
            this.state = state;
            this.function = function;
        }

        @Override
        public DataSet emit(DataSet record) {
            synchronized (state) {
                Object value = state.get();
                Object result = function.call(cx,
                        function.getParentScope(),
                        toScriptable(value),
                        new Object[]{wrapObject(record), value});
                state.set(result);
            }
            return record;
        }

        public Object getState() {
            return state.get();
        }
    }

    private Scriptable toScriptable(Object state) {
        return state instanceof Scriptable ? (Scriptable) state : null;
    }
}
