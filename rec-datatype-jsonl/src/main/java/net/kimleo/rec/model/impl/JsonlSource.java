package net.kimleo.rec.model.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kimleo.rec.data.ColumnVector;
import net.kimleo.rec.data.DataRow;
import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.data.DataType;
import net.kimleo.rec.data.Schema;
import net.kimleo.rec.model.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Reads JSON Lines files into a stream of DataSet.
 * Each line is a JSON object; field names are taken from the first line.
 */
public class JsonlSource implements Source {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonlSource.class);

    private final BufferedReader reader;
    private final ObjectMapper mapper;

    public JsonlSource(Reader reader) {
        this.reader = new BufferedReader(reader);
        this.mapper = new ObjectMapper();
    }

    public JsonlSource(File file) {
        try {
            this.reader = Files.newBufferedReader(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Cannot open JSONL file: " + file.getName(), e);
        }
        this.mapper = new ObjectMapper();
    }

    @Override
    public Stream<DataSet> stream() {
        return reader.lines().map(line -> {
            try {
                JsonNode node = mapper.readTree(line);
                if (node.isObject()) {
                    return parseObject(node);
                }
                throw new IllegalArgumentException("JSONL line is not an object: " + line);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse JSONL line", e);
            }
        });
    }

    private DataSet parseObject(JsonNode node) {
        List<String> names = new ArrayList<>();
        List<DataType> types = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            names.add(entry.getKey());
            parseJsonValue(entry.getValue(), types, values);
        }

        Schema schema = Schema.typed(names, types);
        ColumnVector[] vectors = new ColumnVector[names.size()];
        for (int i = 0; i < names.size(); i++) {
            ColumnVector.Builder vb = ColumnVector.builder(types.get(i), 1);
            vb.add(values.get(i));
            vectors[i] = vb.build();
        }
        return new DataRow(schema, vectors, 0);
    }

    private void parseJsonValue(JsonNode val,
                                List<DataType> types,
                                List<Object> values) {
        if (val.isNull()) {
            types.add(DataType.STRING);
            values.add(null);
        } else if (val.isInt()) {
            types.add(DataType.INT);
            values.add(val.intValue());
        } else if (val.isLong()) {
            types.add(DataType.LONG);
            values.add(val.longValue());
        } else if (val.isDouble() || val.isFloat()) {
            types.add(DataType.DOUBLE);
            values.add(val.doubleValue());
        } else if (val.isBoolean()) {
            types.add(DataType.BOOLEAN);
            values.add(val.booleanValue());
        } else {
            types.add(DataType.STRING);
            values.add(val.asText());
        }
    }
}
