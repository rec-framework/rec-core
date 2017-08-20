package net.kimleo.rec.v2.execution;

import net.kimleo.rec.v2.model.Source;
import net.kimleo.rec.v2.model.Target;

public interface RestartableSource<T> extends Source<T> {

    ExecutionContext context();

    @Override
    default void to(Target<T> target) {
        try {
            stream().forEach(target::put);
        } catch (Throwable ex) {
            context().persist(ex);
        }
    }
}
