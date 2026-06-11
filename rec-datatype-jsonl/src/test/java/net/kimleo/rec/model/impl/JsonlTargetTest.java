package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.DataRow;
import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.data.DataUtils;
import net.kimleo.rec.data.Schema;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JsonlTargetTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldWriteJsonlFile() throws Exception {
        File output = folder.newFile("output.jsonl");
        JsonlTarget target = new JsonlTarget(output);

        Schema schema = Schema.of("name", "age");
        DataSet row1 = DataUtils.createRow(new String[]{"alice", "30"}, schema);
        DataSet row2 = DataUtils.createRow(new String[]{"bob", "25"}, schema);

        target.put(row1);
        target.put(row2);
        target.close();

        List<String> lines = Files.readAllLines(output.toPath());
        assertThat(lines.size(), is(2));
        assertThat(lines.get(0).contains("alice"), is(true));
        assertThat(lines.get(1).contains("bob"), is(true));
    }

    @Test
    public void shouldRoundTripWithJsonlSource() throws Exception {
        File output = folder.newFile("roundtrip.jsonl");
        JsonlTarget target = new JsonlTarget(output);

        Schema schema = Schema.of("name", "age");
        target.put(DataUtils.createRow(new String[]{"alice", "30"}, schema));
        target.put(DataUtils.createRow(new String[]{"bob", "25"}, schema));
        target.close();

        JsonlSource source = new JsonlSource(output);
        List<DataSet> rows = source.stream()
                .collect(java.util.stream.Collectors.toList());

        assertThat(rows.size(), is(2));
        assertThat(rows.get(0).getString("name"), is("alice"));
        assertThat(rows.get(1).getString("name"), is("bob"));
    }
}
