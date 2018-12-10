package net.kimleo.rec.v2.execution;

import org.mozilla.javascript.Context;

@Deprecated
public interface ExecutionContext {
    void commit();
    void persist(Throwable causedBy);

    ExecutionContext restart();

    boolean isNative();
    boolean isCloud();

    Context jsContext();
}
