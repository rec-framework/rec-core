package net.kimleo.rec.logging;

import static net.kimleo.rec.logging.LoggingLevel.DEBUG;
import static net.kimleo.rec.logging.LoggingLevel.INFO;
import static net.kimleo.rec.logging.LoggingLevel.TRACE;
import static net.kimleo.rec.logging.LoggingLevel.WARN;
import static net.kimleo.rec.logging.LoggingLevel.ERROR;

public interface Logger {
    default void trace(String msg) {
        log(TRACE, msg);
    }
    default void debug(String msg) {
        log(DEBUG, msg);
    }
    default void info(String msg) {
        log(INFO, msg);
    }
    default void warn(String msg) {
        log(WARN, msg);
    }
    default void error(String msg) {
        log(ERROR, msg);
    }

    void log(LoggingLevel level, String msg);
}
