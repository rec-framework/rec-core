package net.kimleo.rec.logging.impl;

import net.kimleo.rec.exception.InitializationException;
import net.kimleo.rec.logging.LogAppender;
import net.kimleo.rec.logging.LogFormatter;
import net.kimleo.rec.logging.Logger;
import net.kimleo.rec.logging.LoggingLevel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class LogManager {

    private static LogManager logManager = new LogManager();
    private final LoggingLevel level;
    private final LogFormatter formatter;
    private final LogAppender appender;
    private final Map<String, Logger> loggers;

    private LogManager() {

        Properties properties = new Properties();
        try {
            InputStream ins = getClass().getClassLoader().getResourceAsStream("logging.properties");
            properties.load(ins);
        } catch (IOException e) {
            throw new InitializationException("Unexpected exception when loading logger configs", e);
        }

        try {
            level = LoggingLevel.valueOf(properties.getProperty("log.level", "INFO"));
            formatter = (LogFormatter) Class.forName(
                    properties.getProperty("log.formatter", SimpleLogFormatter.class.getName())).newInstance();
            appender = new PlainFileAppender(new File(getLoggingFile(properties)));
        } catch (Exception ex) {
            throw new InitializationException("Error when initialize LogManager", ex);
        }
        loggers = new LinkedHashMap<>();
    }

    private String getLoggingFile(Properties properties) {
        String pattern = properties.getProperty("log.file", "rec-{time}.log");
        return pattern.replace("{time}", String.valueOf(new Date().getTime()));
    }

    private static LogManager getInstance() {
        if (logManager == null) {
            logManager = new LogManager();
        }
        return logManager;
    }

    public static Logger logger(String loggerName) {
        return getInstance().getLogger(loggerName);
    }

    private Logger getLogger(String name) {
        if (!loggers.containsKey(name)) {
            loggers.put(name, new DefaultLogger(name, level, formatter, appender));
        }
        return loggers.get(name);
    }
}
