package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.data.DataUtils;
import net.kimleo.rec.data.Schema;
import net.kimleo.rec.data.DataType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ParquetSourceTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldWriteAndReadParquet() throws Exception {
        File parquetFile = folder.newFile("test.parquet");

        Schema schema = Schema.typed(
                Arrays.asList("name", "age"),
                Arrays.asList(DataType.STRING, DataType.INT));

        ParquetTarget target = new ParquetTarget(parquetFile);
        target.put(DataUtils.createRow(new String[]{"alice", "30"}, schema));
        target.put(DataUtils.createRow(new String[]{"bob", "25"}, schema));
        target.put(DataUtils.createRow(new String[]{"charlie", "35"}, schema));
        target.close();

        ParquetSource source = new ParquetSource(parquetFile);
        List<DataSet> rows = source.stream().collect(Collectors.toList());

        assertThat(rows.size(), is(3));
        assertThat(rows.get(0).getString("name"), is("alice"));
        assertThat(rows.get(0).getInt("age"), is(30));
        assertThat(rows.get(1).getString("name"), is("bob"));
        assertThat(rows.get(1).getInt("age"), is(25));
        assertThat(rows.get(2).getString("name"), is("charlie"));
        assertThat(rows.get(2).getInt("age"), is(35));
    }

    @Test
    public void shouldHandleMultipleTypes() throws Exception {
        File parquetFile = folder.newFile("types.parquet");

        Schema schema = Schema.typed(
                Arrays.asList("id", "score", "active"),
                Arrays.asList(DataType.LONG, DataType.DOUBLE, DataType.BOOLEAN));

        ParquetTarget target = new ParquetTarget(parquetFile);
        target.put(DataUtils.createRow(
                new String[]{"100", "3.14", "true"}, schema));
        target.close();

        ParquetSource source = new ParquetSource(parquetFile);
        DataSet row = source.stream().findFirst().get();

        assertThat(row.getLong("id"), is(100L));
        assertThat(row.getDouble("score"), is(3.14));
        assertThat(row.getBoolean("active"), is(true));
    }
}
