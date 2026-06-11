package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.DataRow;
import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.data.DataUtils;
import net.kimleo.rec.data.Schema;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InMemoryCacheTeeTest {

    private static DataSet makeRecord(String value) {
        Schema schema = Schema.of("value");
        return DataUtils.createRow(new String[]{value}, schema);
    }

    @Test
    public void shouldCacheRecords() {
        InMemoryCacheTee cache = new InMemoryCacheTee();

        cache.emit(makeRecord("a"));
        cache.emit(makeRecord("b"));
        cache.emit(makeRecord("c"));

        assertThat(cache.size(), is(3));
    }

    @Test
    public void shouldReplayFromSource() {
        InMemoryCacheTee cache = new InMemoryCacheTee();

        cache.emit(makeRecord("x"));
        cache.emit(makeRecord("y"));
        cache.emit(makeRecord("z"));

        List<DataSet> result = cache.source().stream().collect(Collectors.toList());

        assertThat(result.size(), is(3));
        assertThat(result.get(0).getString("value"), is("x"));
        assertThat(result.get(1).getString("value"), is("y"));
        assertThat(result.get(2).getString("value"), is("z"));
    }

    @Test
    public void shouldEvictOldestWhenMaxSizeReached() {
        InMemoryCacheTee cache = new InMemoryCacheTee(3);

        cache.emit(makeRecord("a"));
        cache.emit(makeRecord("b"));
        cache.emit(makeRecord("c"));
        cache.emit(makeRecord("d")); // evicts "a"

        assertThat(cache.size(), is(3));

        List<DataSet> result = cache.source().stream().collect(Collectors.toList());
        assertThat(result.get(0).getString("value"), is("b"));
        assertThat(result.get(1).getString("value"), is("c"));
        assertThat(result.get(2).getString("value"), is("d"));
    }

    @Test
    public void shouldSupportUnlimitedCapacity() {
        InMemoryCacheTee cache = new InMemoryCacheTee();

        for (int i = 0; i < 1000; i++) {
            cache.emit(makeRecord(String.valueOf(i)));
        }

        assertThat(cache.size(), is(1000));
    }

    @Test
    public void shouldClearCache() {
        InMemoryCacheTee cache = new InMemoryCacheTee();

        cache.emit(makeRecord("a"));
        cache.emit(makeRecord("b"));
        cache.clear();

        assertThat(cache.size(), is(0));
        assertThat(cache.source().stream().count(), is(0L));
    }

    @Test
    public void shouldBeReentrant() {
        InMemoryCacheTee cache = new InMemoryCacheTee();

        cache.emit(makeRecord("first"));
        cache.emit(makeRecord("second"));

        // First read
        List<DataSet> first = cache.source().stream().collect(Collectors.toList());
        // Second read should return same data
        List<DataSet> second = cache.source().stream().collect(Collectors.toList());

        assertThat(first.size(), is(second.size()));
        assertThat(first.get(0).getString("value"), is(second.get(0).getString("value")));
        assertThat(first.get(1).getString("value"), is(second.get(1).getString("value")));
    }

    @Test
    public void shouldReturnRecordFromEmit() {
        InMemoryCacheTee cache = new InMemoryCacheTee();

        DataSet record = makeRecord("hello");
        DataSet result = cache.emit(record);

        assertThat(result.getString("value"), is("hello"));
    }
}