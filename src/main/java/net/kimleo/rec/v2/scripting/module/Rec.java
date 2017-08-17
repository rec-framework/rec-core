package net.kimleo.rec.v2.scripting.module;

import net.kimleo.rec.common.concept.Mapped;
import net.kimleo.rec.logging.Logger;
import net.kimleo.rec.logging.impl.LogManager;
import net.kimleo.rec.sepval.parser.ParseConfig;
import net.kimleo.rec.v2.execution.ExecutionContext;
import net.kimleo.rec.v2.execution.impl.CountBasedExecutionContext;
import net.kimleo.rec.v2.execution.impl.CountBasedRestartableSource;
import net.kimleo.rec.v2.model.Source;
import net.kimleo.rec.v2.model.Target;
import net.kimleo.rec.v2.model.Tee;
import net.kimleo.rec.v2.model.impl.BufferedCachingTee;
import net.kimleo.rec.v2.model.impl.CSVFileSource;
import net.kimleo.rec.v2.model.impl.CollectTee;
import net.kimleo.rec.v2.model.impl.FlatFileTarget;
import net.kimleo.rec.v2.model.impl.ItemCounterTee;
import net.kimleo.rec.v2.model.impl.ResultSetSource;
import net.kimleo.rec.v2.scripting.model.JSRecord;
import net.kimleo.rec.v2.scripting.model.State;
import net.kimleo.rec.v2.utils.Persistence;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

public class Rec {
    private static final Logger LOGGER = LogManager.logger(Rec.class.getName());
    private static String scriptPath;
    private static Context context;
    private static ExecutionContext executionContext;
    private static boolean enableRetry;


    public static void initializeContext(Context context) {
        scriptPath = (String) context.getThreadLocal("SCRIPT_PATH");
        Rec.context = context;
        LOGGER.info("Initialized Rec context at " + scriptPath);
    }

    // Wrappers
    public static <T> Consumer<T> action(Function function) {
        LOGGER.info(format("Action wrapper created for Function #[%d]", function.hashCode()));
        return (object) ->
                function.call(context, function.getParentScope(), null, new Object[]{ wrap(object) });
    }

    public static  <T> Predicate<T> pred(Function function) {
        LOGGER.info(format("Predicate wrapper created for Function #[%d]", function.hashCode()));
        return (func) ->
                (boolean) function.call(context, function.getParentScope(), null, new Object[]{ wrap(func) });
    }

    @SuppressWarnings("unchecked")
    private static <T> Object wrap(T obj) {
        if (obj instanceof Mapped) {
            return new JSRecord((Mapped<String>) obj);
        }
        return obj;
    }

    public static void println(Object... objs) {
        for (Object obj : objs) {
            System.out.println(obj);
        }
    }

    // Sources
    public static Source stream(Stream<Mapped<String>> stream) {
        LOGGER.info(format("Created source from Stream #[%d]", stream.hashCode()));
        return () -> stream;
    }

    public static Source resultSet(ResultSet rs) {
        LOGGER.info(format("Created source from ResultSet #[%d]", rs.hashCode()));
        return new ResultSetSource(rs);
    }

    public static Source csv(String file, String accessors) {
        LOGGER.info(format("Loading file #[%s] with accessors: [%s]", file, accessors));

        ParseConfig config;
        String extension = file.substring(file.lastIndexOf('.'));
        switch (extension) {
            case ".csv":
                config = ParseConfig.DEFAULT;
                break;
            case ".dsv":
                config = new ParseConfig(';');
                break;
            case ".psv":
                config = new ParseConfig(':');
                break;
            default:
                String msg = format("Unexpected extension of file: [%s]", file);
                LOGGER.error(msg);
                throw new IllegalArgumentException(msg);
        }

        return new CSVFileSource(Paths.get(scriptPath, file).toFile(), accessors, config);
    }

    // Targets
    public static Target dummy() {
        LOGGER.info("Dummy target created");
        return record -> {};
    }

    public static Target target(Function function) {
        LOGGER.info(format("Wrapper target created with Function #[%d]", function.hashCode()));
        return (record) ->
                function.call(context, function.getParentScope(), null, new Object[] { wrap(record)});
    }

    public static Target flat(String filename) {
        LOGGER.info(format("FlatFileTarget created under name: #[%s]", filename));
        return new FlatFileTarget(Paths.get(scriptPath, filename).toFile());
    }

    // Tees
    public static Tee counter(Function predicate) {
        return new ItemCounterTee(pred(predicate));
    }

    public static Tee stateless(Function function) {
        return record -> {
            function.call(context, function.getParentScope(), null, new Object[] { wrap(record)} );
            return record;
        };
    }

    public static Tee stateful(Scriptable initial, Function function) {
        State state = new State(initial);

        return new StatefulTee(state, function);
    }

    public static <T> Source<T> restartable(Source<T> source) {
        return new CountBasedRestartableSource<>(source,
                enableRetry ? executionContext : new CountBasedExecutionContext(0));
    }

    private static Scriptable toScriptable(Object state) {
        return state instanceof Scriptable ? (Scriptable) state : null;
    }

    public static Tee cache(int size) {
        return new BufferedCachingTee(size);
    }

    public static Tee collect(Collection<Mapped<String>> collect) {
        return new CollectTee(collect);
    }

    public static Tee<Mapped<String>> unique(String... keys) {
        HashSet<List<String>> sets = new HashSet<>();

        return record -> {
            List<String> fields = Arrays.stream(keys)
                    .map(record::get)
                    .collect(Collectors.toList());

            if (sets.contains(fields)) {
                throw new IllegalStateException(
                        format("Uniqueness checking failed for fields: [%s]; values: [%s]",
                                Arrays.stream(keys).collect(Collectors.joining(", ")),
                                fields.stream().collect(Collectors.joining(", "))));
            } else {
                sets.add(fields);
            }
            return record;
        };
    }

    public static void setExecutionContext(String retryfile) throws IOException, ClassNotFoundException {
        LOGGER.info(format("Loading execution context from %s", retryfile));
        if (retryfile != null && !retryfile.isEmpty()) {
            setExecutionContext(((ExecutionContext) Persistence.loadObjectFromFile(retryfile)).restart());
            LOGGER.info(format("Loaded execution context %s", executionContext));
        }
    }

    public static void setExecutionContext(ExecutionContext context) throws IOException, ClassNotFoundException {
        Rec.enableRetry = true;
        Rec.executionContext = context;
    }

    public static class StatefulTee<T> implements Tee<T> {
        private final State state;
        private final Function function;

        StatefulTee(State state, Function function) {
            this.state = state;
            this.function = function;
        }

        @Override
        public T emit(T record) {
            synchronized (state) {
                Object value = state.get();
                Object result = function.call(context,
                        function.getParentScope(),
                        toScriptable(value),
                        new Object[]{wrap(record), value});
                state.set(result);
            }
            return record;
        }

        public Object getState() {
            LOGGER.info(format("Getting Stateful Tee #[%s]", toString()));
            return state.get();
        }
    }

}
