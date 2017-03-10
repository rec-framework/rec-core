package net.kimleo.rec.logging.impl;

import net.kimleo.rec.logging.LogAppender;
import net.kimleo.rec.logging.LogFormatter;
import net.kimleo.rec.logging.Logger;
import net.kimleo.rec.logging.LoggingLevel;

public class DefaultLogger implements Logger {
    private final String name;
    private final LoggingLevel loggingLevel;
    private final LogFormatter logFormatter;
    private LogAppender appender;

    public DefaultLogger(String name, LoggingLevel loggingLevel, LogFormatter logFormatter, LogAppender appender) {
        this.name = name;
        this.loggingLevel = loggingLevel;
        this.logFormatter = logFormatter;
        this.appender = appender;
    }

    @Override
    public void log(LoggingLevel level, String msg) {
        if (level.level() >= loggingLevel.level()) {
            appender.append(logFormatter.format(name, level, msg));
        }
    }
}
