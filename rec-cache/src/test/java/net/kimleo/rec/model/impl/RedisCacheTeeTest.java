package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.data.DataUtils;
import net.kimleo.rec.data.Schema;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class RedisCacheTeeTest {

    private DataSet record(String... pairs) {
        String[] fields = new String[pairs.length / 2];
        String[] values = new String[pairs.length / 2];
        for (int i = 0; i < pairs.length; i += 2) {
            fields[i / 2] = pairs[i];
            values[i / 2] = pairs[i + 1];
        }
        Schema schema = Schema.of(fields);
        return DataUtils.createRow(values, schema);
    }

    @Test
    public void shouldCacheRecords() {
        RedisCacheTee cache = new RedisCacheTee("redis://localhost");

        cache.emit(record("name", "alice", "age", "30"));
        cache.emit(record("name", "bob", "age", "25"));

        assertThat(cache.size(), is(2));
    }

    @Test
    public void shouldGenerateKeysFromRecord() {
        RedisCacheTee cache = new RedisCacheTee("redis://localhost", "test:", 60);

        cache.emit(record("id", "1", "name", "alice"));

        String value = cache.get("test:id=1:name=alice:");
        assertThat(value, notNullValue());
        assertThat(value, is("{\"id\":\"1\",\"name\":\"alice\"}"));
    }

    @Test
    public void shouldReplayFromSource() {
        RedisCacheTee cache = new RedisCacheTee("redis://localhost");

        cache.emit(record("a", "1"));
        cache.emit(record("b", "2"));

        List<DataSet> result = cache.source().stream().collect(Collectors.toList());

        assertThat(result.size(), is(2));
        assertThat(result.get(0).getString("a"), is("1"));
        assertThat(result.get(1).getString("b"), is("2"));
    }

    @Test
    public void shouldClearCache() {
        RedisCacheTee cache = new RedisCacheTee("redis://localhost");

        cache.emit(record("x", "1"));
        cache.clear();

        assertThat(cache.size(), is(0));
    }

    @Test
    public void shouldSerializeToJson() {
        RedisCacheTee cache = new RedisCacheTee("redis://localhost");

        cache.emit(record("name", "alice", "city", "beijing"));

        String json = cache.get("rec:cache:name=alice:city=beijing:");
        assertThat(json, is("{\"name\":\"alice\",\"city\":\"beijing\"}"));
    }
}