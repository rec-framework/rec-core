package net.kimleo.rec.v2.scripting.model;

import net.kimleo.rec.concept.Mapped;
import net.kimleo.rec.sepval.parser.ParseConfig;
import net.kimleo.rec.v2.model.Source;
import net.kimleo.rec.v2.model.Target;
import net.kimleo.rec.v2.model.Tee;
import net.kimleo.rec.v2.model.impl.*;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Rec {
    private String scriptPath;
    private final Context context;
    private final Scriptable scope;

    public Rec(Context context, Scriptable scope) {
        scriptPath = (String) context.getThreadLocal("SCRIPT_PATH");
        this.context = context;
        this.scope = scope;
    }

    // Wrappers
    public <T> Consumer<T> action(Function function) {
        return (object) -> {
            function.call(context, function.getParentScope(), null, new Object[]{ object });
        };
    }

    public <T> Predicate<T> pred(Function function) {
        return (record) ->
                (boolean) function.call(context, function.getParentScope(), null, new Object[]{ record });
    }

    public void println(Object... objs) {
        for (Object obj : objs) {
            System.out.println(obj);
        }
    }

    // Sources
    public Source stream(Stream<Mapped<String>> stream) {
        return () -> stream;
    }

    public Source csv(String file, String accessors) {
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
                throw new IllegalArgumentException("Unexpected extension of file: [" + file + "]");
        }

        return new CSVFileSource(Paths.get(scriptPath, file).toFile(), accessors, config);
    }

    // Targets
    public Target dummy() {
        return record -> {};
    }

    public Target target(Function function) {
        return (record) -> {
            function.call(context, function.getParentScope(), null, new Object[] {record});
        };
    }

    public Target flat(String filename) {
        return new FlatFileTarget(Paths.get(scriptPath, filename).toFile());
    }

    // Tees
    public Tee counter(Function predicate) {
        return new ItemCounterTee(pred(predicate));
    }

    public Tee cache(int size) {
        return new BufferedCachingTee(size);
    }

    public Tee collect(Collection<Mapped<String>> collect) {
        return new CollectTee(collect);
    }

    public Tee unique(String... keys) {
        HashSet<List<String>> sets = new HashSet<>();

        return record -> {
            List<String> fields = Arrays.stream(keys)
                    .map(record::get)
                    .collect(Collectors.toList());

            if (sets.contains(fields)) {
                throw new IllegalStateException(
                        String.format("Uniqueness checking failed for fields: [%s]; values: [%s]",
                                Arrays.stream(keys).collect(Collectors.joining(", ")),
                                fields.stream().collect(Collectors.joining(", "))));
            } else {
                sets.add(fields);
            }
            return record;
        };
    }
}
