package net.kimleo.rec.logging.impl;

import net.kimleo.rec.exception.InitializationException;
import net.kimleo.rec.logging.LogAppender;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PlainFileAppender implements LogAppender {

    private final PrintWriter writer;

    public PlainFileAppender(File file) {
        try {
            File logsDir = new File("logs");
            if (!logsDir.exists()) {
                logsDir.mkdir();
            }
            this.writer = new PrintWriter(Files.newBufferedWriter(Paths.get(logsDir.getName(), file.getName())));

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                writer.flush();
                writer.close();
            }));
        } catch (IOException e) {
            throw new InitializationException("Unable to initialize a file appender on: [" + file.getName() + "]", e);
        }
    }

    @Override
    public void append(String logEntry) {
        writer.println(logEntry);
    }
}
