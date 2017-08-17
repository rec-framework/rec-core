package net.kimleo.rec.v2.execution.impl;

import net.kimleo.rec.exception.ResourceAccessException;
import net.kimleo.rec.logging.Logger;
import net.kimleo.rec.logging.impl.LogManager;
import net.kimleo.rec.v2.execution.ExecutionContext;
import net.kimleo.rec.v2.utils.Persistence;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import static java.lang.String.format;

public final class CountBasedExecutionContext implements ExecutionContext, Serializable {
    public static final long serialVersionUID = 1453356753764L;
    private int count = 0;
    private final int baseCount;

    private static Logger LOGGER = LogManager.logger(CountBasedExecutionContext.class.getName());

    public CountBasedExecutionContext(int baseCount) {
        this.baseCount = baseCount;
        LOGGER.info(format("Initialize execution context based on count %d", baseCount));
    }

    @Override
    public boolean ready() {
        return count >= baseCount;
    }

    @Override
    public int state() {
        return baseCount;
    }

    @Override
    public void commit() {
        count ++;
    }

    @Override
    public void persist(Throwable causedBy) {
        try {
            String filename = String.valueOf(new Date().getTime()).concat(".retry");
            Persistence.saveObjectToFile(this, filename);
            LOGGER.info(format("Successfully save context to [%s]", filename));
        } catch (IOException e) {
            LOGGER.error(format("Unable to save execution context and the count is %d", count));
            throw new ResourceAccessException("Cannot persist execution context", e);
        }
        throw new ResourceAccessException(format("Need retry on count %d", baseCount + count), causedBy);
    }

    @Override
    public ExecutionContext restart() {
        return new CountBasedExecutionContext(count + baseCount);
    }

    public int count() {
        return count;
    }

    public static CountBasedExecutionContext initialContext() {
        return new CountBasedExecutionContext(0);
    }

    @Override
    public String toString() {
        return "CountBasedExecutionContext{" +
                "count=" + count +
                ", baseCount=" + baseCount +
                '}';
    }
}
