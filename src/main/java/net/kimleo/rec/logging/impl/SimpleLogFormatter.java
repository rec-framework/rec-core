package net.kimleo.rec.logging.impl;

import net.kimleo.rec.logging.LogFormatter;
import net.kimleo.rec.logging.LoggingLevel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleLogFormatter implements LogFormatter {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

    @Override
    public String format(String name, LoggingLevel level, String message) {
        return String.format("[%s][%s]%s: %s",
                dateFormat.format(new Date()), level, name, message);
    }
}
