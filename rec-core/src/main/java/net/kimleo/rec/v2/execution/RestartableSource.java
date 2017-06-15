package net.kimleo.rec.v2.execution;

import net.kimleo.rec.v2.model.Source;

public interface RestartableSource<T> extends Source<T> {
    ExecutionContext context();
}
