package net.kimleo.rec.model.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.model.Source;
import net.kimleo.rec.model.Target;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

/**
 * Writes DataSet records as JSON Lines (one JSON object per line).
 */
public class JsonlTarget implements Target, Closeable {

    private final PrintWriter writer;
    private final ObjectMapper mapper;

    public JsonlTarget(File file) {
        try {
            writer = new PrintWriter(Files.newBufferedWriter(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException("Cannot open output JSONL file: " + file.getName(), e);
        }
        mapper = new ObjectMapper();
    }

    @Override
    public void put(DataSet record) {
        try {
            StringBuilder sb = new StringBuilder("{");
            for (int i = 0; i < record.columnCount(); i++) {
                if (i > 0) sb.append(",");
                sb.append("\"").append(escape(record.fieldNames().get(i))).append("\":");
                Object val = record.get(i);
                if (val == null) {
                    sb.append("null");
                } else if (val instanceof Number || val instanceof Boolean) {
                    sb.append(val);
                } else {
                    sb.append("\"").append(escape(val.toString())).append("\"");
                }
            }
            sb.append("}");
            writer.println(sb.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize record to JSONL", e);
        }
    }

    @Override
    public void putAll(Source source) {
        source.stream().forEach(this::put);
        writer.flush();
    }

    @Override
    public void close() {
        writer.flush();
        writer.close();
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
