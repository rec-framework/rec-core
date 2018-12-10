package net.kimleo.rec.v2.execution.impl;

import net.kimleo.rec.v2.execution.ExecutionContext;
import net.kimleo.rec.v2.execution.RestartableSource;
import net.kimleo.rec.v2.model.Source;

import java.util.stream.Stream;

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
    public Source<T> skip(int n) {
        return source.skip(n);
    }

    @Override
    public Stream<T> stream() {
        return source.skip(context.skipCount()).stream().peek(it -> context.commit());
    }

    public static <T> Source<T> from(Stream<T> stream) {
        return new CountBasedRestartableSource<>(() -> stream, new ExecutionContext() {});
    }

    public static <T> Source<T> from(Stream<T> stream, ExecutionContext context) {
        return new CountBasedRestartableSource<>(() -> stream, context);
    }
}
