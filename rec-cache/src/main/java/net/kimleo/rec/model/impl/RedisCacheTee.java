package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.model.Source;
import net.kimleo.rec.model.Tee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RedisCacheTee implements Tee {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheTee.class);

    private final String redisUrl;
    private final String keyPrefix;
    private final int ttlSeconds;
    private final List<DataSet> localBuffer = new ArrayList<>();
    private final Map<String, String> store = new LinkedHashMap<>();

    public RedisCacheTee(String redisUrl, String keyPrefix, int ttlSeconds) {
        this.redisUrl = redisUrl;
        this.keyPrefix = keyPrefix;
        this.ttlSeconds = ttlSeconds;
        LOGGER.info("RedisCacheTee initialized: url={}, prefix={}, ttl={}s",
                redisUrl, keyPrefix, ttlSeconds);
    }

    public RedisCacheTee(String redisUrl) {
        this(redisUrl, "rec:cache:", 3600);
    }

    @Override
    public DataSet emit(DataSet record) {
        String key = generateKey(record);
        String value = serialize(record);
        store.put(key, value);
        localBuffer.add(record);
        LOGGER.debug("Cached record with key: {}", key);
        return record;
    }

    @Override
    public Source source() {
        return () -> new ArrayList<>(localBuffer).stream();
    }

    public String get(String key) {
        return store.get(key);
    }

    public int size() {
        return store.size();
    }

    public void clear() {
        store.clear();
        localBuffer.clear();
    }

    private String generateKey(DataSet record) {
        StringBuilder sb = new StringBuilder(keyPrefix);
        for (String field : record.fieldNames()) {
            sb.append(field).append("=").append(record.getString(field)).append(":");
        }
        return sb.toString();
    }

    private String serialize(DataSet record) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (String field : record.fieldNames()) {
            if (!first) sb.append(",");
            sb.append("\"").append(field).append("\":\"").append(record.getString(field)).append("\"");
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
