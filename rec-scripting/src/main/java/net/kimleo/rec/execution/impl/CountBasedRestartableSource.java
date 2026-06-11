package net.kimleo.rec.execution.impl;

import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.execution.ExecutionContext;
import net.kimleo.rec.execution.RestartableSource;
import net.kimleo.rec.model.Source;

import java.util.stream.Stream;

import static net.kimleo.rec.execution.impl.NativeExecutionContext.initialContext;

public class CountBasedRestartableSource implements RestartableSource {

    private final Source source;
    private final NativeExecutionContext context;

    public CountBasedRestartableSource(Source source, NativeExecutionContext context) {
        this.source = source;
        this.context = context;
    }

    @Override
    public ExecutionContext context() {
        return this.context;
    }

    @Override
    public Source skip(int n) {
        return source.skip(n);
    }

    @Override
    public Stream<DataSet> stream() {
        return source.skip(context.state()).stream().map(it -> {
            context.commit();
            return it;
        });
    }

    public static Source from(Stream<? extends DataSet> stream) {
        return new CountBasedRestartableSource(() -> stream.map(DataSet.class::cast), initialContext());
    }

    public static Source from(Stream<? extends DataSet> stream, NativeExecutionContext context) {
        return new CountBasedRestartableSource(() -> stream.map(DataSet.class::cast), context);
    }
}
