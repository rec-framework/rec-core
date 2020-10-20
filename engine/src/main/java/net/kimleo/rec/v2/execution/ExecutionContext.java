package net.kimleo.rec.v2.execution;

public interface ExecutionContext {
    default void commit() {}
    default void persist(Throwable causedBy) {}

    default int skipCount() {
        return 0;
    }
}
