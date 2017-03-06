package net.kimleo.rec.v2.logging.impl;

import net.kimleo.rec.v2.logging.LoggingLevel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleLogFormatter implements net.kimleo.rec.v2.logging.LogFormatter {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

    @Override
    public String format(String name, LoggingLevel level, String message) {
        return String.format("[%s][%s]%s: %s",
                dateFormat.format(new Date()), level, name, message);
    }
}
