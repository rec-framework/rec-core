package net.kimleo.rec.logging.impl;

import net.kimleo.rec.exception.InitializationException;
import net.kimleo.rec.logging.LogAppender;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
            this.writer = writer(file, logsDir);
            closeOnShutdown(writer);
        } catch (IOException e) {
            throw new InitializationException(
                    String.format("Unable to initialize a file appender on: [%s]",
                            file.getName()), e);
        }
    }

    private void closeOnShutdown(final PrintWriter writer) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            writer.flush();
            writer.close();
        }));
    }

    private PrintWriter writer(File file, File logsDir) throws IOException {
        return new PrintWriter(
                Files.newBufferedWriter(
                        Paths.get(logsDir.getName(), file.getName())));
    }

    @Override
    public void append(String logEntry) {
        writer.println(logEntry);
    }
}
