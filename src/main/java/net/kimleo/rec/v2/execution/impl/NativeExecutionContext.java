package net.kimleo.rec.v2.execution.impl;

import net.kimleo.rec.common.exception.ResourceAccessException;
import net.kimleo.rec.v2.execution.ExecutionContext;
import net.kimleo.rec.v2.utils.Persistence;
import org.mozilla.javascript.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import static java.lang.String.format;

public final class NativeExecutionContext implements ExecutionContext, Serializable {
    public static final long serialVersionUID = 1453356753764L;
    private int count = 0;
    private final int baseCount;

    private static final Logger LOGGER = LoggerFactory.getLogger(NativeExecutionContext.class);
    private String scriptPath;
    private transient Context jsContext;
    private transient boolean enableRetry;
    private String retryFile = null;

    public NativeExecutionContext(int baseCount) {
        this.baseCount = baseCount;
        LOGGER.info(format("Initialize execution context based on count %d", baseCount));
    }

    public int state() {
        return baseCount;
    }

    @Override
    public void commit() {
        count ++;
    }

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

    public String scriptPath() {
        return scriptPath;
    }

    public boolean enableRetry() {
        return enableRetry;
    }

    public String retryFile() {
        return retryFile;
    }

    public int count() {
        return count;
    }

    public static NativeExecutionContext initialContext() {
        return new NativeExecutionContext(0);
    }

    @Override
    public String toString() {
        return "NativeExecutionContext{" +
                "count=" + count +
                ", baseCount=" + baseCount +
                '}';
    }

    @Override
    public ExecutionContext restart() {
        NativeExecutionContext context = new NativeExecutionContext(count + baseCount);
        context.setJsContext(jsContext);
        context.setScriptPath(scriptPath);
        context.setEnableRetry(enableRetry);
        return context;
    }

    @Override
    public boolean isNative() {
        return true;
    }

    @Override
    public boolean isCloud() {
        return !isNative();
    }

    @Override
    public Context jsContext() {
        return jsContext;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public void setJsContext(Context jsContext) {
        this.jsContext = jsContext;
    }

    public void setEnableRetry(boolean enableRetry) {
        this.enableRetry = enableRetry;
    }

    public void setRetryFile(String retryFile) {
        this.retryFile = retryFile;
    }
}
