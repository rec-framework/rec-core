package net.kimleo.rec.v2.scripting.module;

import net.kimleo.rec.common.concept.Mapped;
import net.kimleo.rec.logging.Logger;
import net.kimleo.rec.logging.impl.LogManager;
import net.kimleo.rec.sepval.parser.ParseConfig;
import net.kimleo.rec.v2.execution.ExecutionContext;
import net.kimleo.rec.v2.model.Source;
import net.kimleo.rec.v2.model.Target;
import net.kimleo.rec.v2.model.Tee;
import net.kimleo.rec.v2.model.impl.CSVSource;
import net.kimleo.rec.v2.model.impl.CollectTee;
import net.kimleo.rec.v2.model.impl.ItemCounterTee;
import net.kimleo.rec.v2.model.impl.ReactiveTee;
import net.kimleo.rec.v2.model.impl.ResultSetSource;
import net.kimleo.rec.v2.scripting.model.JSRecord;
import net.kimleo.rec.v2.scripting.model.State;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.io.Reader;
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
    protected Context jsContext;

    public Rec(ExecutionContext context) {
        this.jsContext = context.jsContext();
    }

    // Wrappers
    public <T> Consumer<T> action(Function function) {
        LOGGER.info(format("Action wrapper created for Function #[%d]", function.hashCode()));
        return (object) ->
                function.call(jsContext, function.getParentScope(), null, new Object[]{ wrap(object) });
    }

    public  <T> Predicate<T> pred(Function function) {
        LOGGER.info(format("Predicate wrapper created for Function #[%d]", function.hashCode()));
        return (func) ->
                (boolean) function.call(jsContext, function.getParentScope(), null, new Object[]{ wrap(func) });
    }

    @SuppressWarnings("unchecked")
    private static <T> Object wrap(T obj) {
        if (obj instanceof Mapped) {
            return new JSRecord((Mapped<String>) obj);
        }
        return obj;
    }

    // Sources
    public Source stream(Stream<Mapped<String>> stream) {
        LOGGER.info(format("Created source from Stream #[%d]", stream.hashCode()));
        return () -> stream;
    }

    public Source resultSet(ResultSet rs) {
        LOGGER.info(format("Created source from ResultSet #[%d]", rs.hashCode()));
        return new ResultSetSource(rs);
    }

    public Source csv(Reader reader, String delimiter, String accessors) {
        LOGGER.info(format("Loading csv data with accessors: [%s]", accessors));
        ParseConfig config = new ParseConfig(delimiter.charAt(0));
        return new CSVSource(reader, accessors, config);
    }

    public <T> Source<T> reactive() {
        return new ReactiveTee<>(null);
    }

    // Targets
    public Target dummy() {
        LOGGER.info("Dummy target created");
        return record -> {};
    }

    public Target target(Function function) {
        LOGGER.info(format("Wrapper target created with Function #[%d]", function.hashCode()));
        return (record) ->
                function.call(jsContext, function.getParentScope(), null, new Object[] { wrap(record)});
    }

    public Tee counter(Function predicate) {
        return new ItemCounterTee(pred(predicate));
    }

    public Tee stateless(Function function) {
        return record -> {
            function.call(jsContext, function.getParentScope(), null, new Object[] { wrap(record)} );
            return record;
        };
    }

    public Tee stateful(Scriptable initial, Function function) {
        State state = new State(initial);

        return new StatefulTee(state, function);
    }

    public <T> Tee<T> collect(Collection<T> collect) {
        return new CollectTee<>(collect);
    }

    public Tee<Mapped<String>> unique(String... keys) {
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

    public class StatefulTee<T> implements Tee<T> {

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
                Object result = function.call(jsContext,
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

    @Override
    public String toString() {
        return "Rec{}";
    }

    private Scriptable toScriptable(Object state) {
        return state instanceof Scriptable ? (Scriptable) state : null;
    }

}
