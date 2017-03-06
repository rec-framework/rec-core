package net.kimleo.rec.v2.logging;

public interface LogFormatter {
    String format(String name, LoggingLevel level, String message);
}
