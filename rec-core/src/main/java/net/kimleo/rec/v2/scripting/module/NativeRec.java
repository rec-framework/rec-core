package net.kimleo.rec.v2.scripting.module;

import net.kimleo.rec.exception.ResourceAccessException;
import net.kimleo.rec.logging.Logger;
import net.kimleo.rec.logging.impl.LogManager;
import net.kimleo.rec.v2.execution.ExecutionContext;
import net.kimleo.rec.v2.execution.impl.CountBasedRestartableSource;
import net.kimleo.rec.v2.execution.impl.NativeExecutionContext;
import net.kimleo.rec.v2.model.Source;
import net.kimleo.rec.v2.model.Target;
import net.kimleo.rec.v2.model.Tee;
import net.kimleo.rec.v2.model.impl.BufferedCachingTee;
import net.kimleo.rec.v2.model.impl.FlatFileTarget;
import net.kimleo.rec.v2.utils.Persistence;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.String.format;

public class NativeRec extends Rec {

    private static final Logger LOGGER = LogManager.logger(NativeRec.class.getName());

    private String scriptPath;
    private NativeExecutionContext executionContext;


    public NativeRec(ExecutionContext context) {
        super(context);
        executionContext = (NativeExecutionContext) context;
        scriptPath = executionContext.scriptPath();
        LOGGER.info("Initialized Rec context at " + scriptPath);
    }

    public Reader file(String filename) {
        try {
            return Files.newBufferedReader(Paths.get(scriptPath, filename));
        } catch (IOException e) {
            LOGGER.error(String.format("File %s cannot be found", filename));
            throw new ResourceAccessException("File cannot be found", e);
        }
    }

    public Tee cache(int size) {
        return new BufferedCachingTee(size);
    }

    public Target flat(String filename) {
        LOGGER.info(format("FlatFileTarget created under name: #[%s]", filename));
        return new FlatFileTarget(Paths.get(scriptPath, filename).toFile());
    }

    public <T> Source<T> restartable(Source<T> source) {
        if (executionContext.enableRetry()) {
            try {
                loadRetryContext(executionContext.retryFile());
            } catch (Exception ignored) {
            }
        }
        return new CountBasedRestartableSource<>(source, executionContext);
    }


    private void loadRetryContext(String retryfile) throws IOException, ClassNotFoundException {
        LOGGER.info(format("Loading execution context from %s", retryfile));
        if (retryfile != null && !retryfile.isEmpty()) {
            NativeExecutionContext executionContext = (NativeExecutionContext) Persistence.loadObjectFromFile(retryfile);
            executionContext.setJsContext(jsContext);
            executionContext.setScriptPath(scriptPath);
            this.executionContext = (NativeExecutionContext) executionContext.restart();
            LOGGER.info(format("Loaded execution context %s", this.executionContext));
        }
    }

    public void println(Object... objs) {
        for (Object obj : objs) {
            System.out.println(obj);
        }
    }

}