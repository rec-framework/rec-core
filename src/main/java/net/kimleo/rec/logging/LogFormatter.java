package net.kimleo.rec.logging;

public interface LogFormatter {
    String format(String name, LoggingLevel level, String message);
}
