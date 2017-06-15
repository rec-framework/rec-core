package net.kimleo.rec.v2.execution.impl;

import net.kimleo.rec.v2.execution.ExecutionContext;

public final class CountBasedExecutionContext implements ExecutionContext {

    private int count = 0;
    private final int baseCount;

    public CountBasedExecutionContext(int baseCount) {
        this.baseCount = baseCount;
    }

    @Override
    public boolean ready() {
        return count >= baseCount;
    }

    @Override
    public void commit() {
        count ++;
    }

    public static CountBasedExecutionContext initialContext() {
        return new CountBasedExecutionContext(0);
    }
}
