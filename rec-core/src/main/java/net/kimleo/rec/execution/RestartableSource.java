package net.kimleo.rec.execution;

import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.model.Source;
import net.kimleo.rec.model.Target;

public interface RestartableSource extends Source {

    ExecutionContext context();

    @Override
    default void to(Target target) {
        try {
            stream().forEach(target::put);
        } catch (Throwable ex) {
            context().persist(ex);
        }
    }
}
