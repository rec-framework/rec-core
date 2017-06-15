package net.kimleo.rec.v2.execution.impl;

import net.kimleo.rec.concept.Mapped;
import net.kimleo.rec.v2.execution.ExecutionContext;
import net.kimleo.rec.v2.execution.RestartableSource;
import net.kimleo.rec.v2.model.Source;

import java.util.stream.Stream;

import static net.kimleo.rec.v2.execution.impl.CountBasedExecutionContext.initialContext;

public class CountBasedRestartableSource<T> implements RestartableSource<T> {

    private final Source<T> source;
    private final ExecutionContext context;

    public CountBasedRestartableSource(Source<T> source, ExecutionContext context) {
        this.source = source;
        this.context = context;
    }

    @Override
    public ExecutionContext context() {
        return this.context;
    }

    @Override
    public Stream<T> stream() {
        return source.stream().filter(s -> {
            boolean ready = context.ready();
            context.commit();
            return ready;
        });
    }

    public static <T> Source<T> from(Stream<T> stream) {
        return new CountBasedRestartableSource<>(() -> stream, initialContext());
    }

    public static <T> Source<T> from(Stream<T> stream, CountBasedExecutionContext context) {
        return new CountBasedRestartableSource<>(() -> stream, context);
    }
}
