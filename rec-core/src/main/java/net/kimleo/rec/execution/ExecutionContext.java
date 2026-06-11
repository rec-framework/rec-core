package net.kimleo.rec.execution;

public interface ExecutionContext {
    void commit();
    void persist(Throwable causedBy);

    ExecutionContext restart();

    boolean isNative();
    boolean isCloud();

    Object jsContext();
}
