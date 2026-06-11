package net.kimleo.rec.scripting.module;

import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.model.Source;
import net.kimleo.rec.model.Target;
import net.kimleo.rec.model.Tee;
import net.kimleo.rec.model.impl.CSVSource;
import net.kimleo.rec.model.impl.CollectTee;
import net.kimleo.rec.model.impl.ItemCounterTee;
import net.kimleo.rec.sepval.parser.ParseConfig;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.function.Predicate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * Core pipeline methods that need Rhino Function wrapping.
 * Installed directly by Scripting (not via RecPlugin).
 */
public class CoreRecPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreRecPlugin.class);

    private final Context cx;
    private final Rec rec;

    public CoreRecPlugin(Context cx, Rec rec) {
        this.cx = cx;
        this.rec = rec;
    }

    public Source csv(Reader reader, String delimiter, String accessors) {
        LOGGER.info("Loading csv data with accessors: [{}]", accessors);
        ParseConfig config = new ParseConfig(delimiter.charAt(0));
        return new CSVSource(reader, accessors, config);
    }

    public Source stream(Stream<DataSet> stream) {
        LOGGER.info("Created source from Stream #{}", stream.hashCode());
        return () -> stream;
    }

    public Target dummy() {
        LOGGER.info("Dummy target created");
        return record -> {};
    }

    public Target target(Function function) {
        LOGGER.info("Wrapper target created with Function #{}", function.hashCode());
        return (record) ->
                function.call(cx, function.getParentScope(), null, new Object[]{ Rec.wrapObject(record) });
    }

    public Tee counter(Function predicate) {
        Predicate<Object> pred = rec.pred(predicate);
        return new ItemCounterTee(r -> pred.test(r));
    }

    public Tee stateless(Function function) {
        return record -> {
            function.call(cx, function.getParentScope(), null, new Object[]{ Rec.wrapObject(record) });
            return record;
        };
    }

    public CollectTee collect() {
        return new CollectTee();
    }

    public Tee unique(String... keys) {
        HashSet<List<String>> sets = new HashSet<>();
        return (DataSet record) -> {
            List<String> fields = Arrays.stream(keys)
                    .map(record::getString)
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
}
