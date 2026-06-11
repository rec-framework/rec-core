package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.DataSet;
import org.junit.Test;

import java.io.File;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JsonlSourceTest {

    @Test
    public void shouldReadJsonlLines() {
        String jsonl = "{\"name\":\"alice\",\"age\":30}\n" +
                "{\"name\":\"bob\",\"age\":25}\n";

        JsonlSource source = new JsonlSource(new StringReader(jsonl));
        List<DataSet> rows = source.stream().collect(Collectors.toList());

        assertThat(rows.size(), is(2));
        assertThat(rows.get(0).getString("name"), is("alice"));
        assertThat(rows.get(0).getInt("age"), is(30));
        assertThat(rows.get(1).getString("name"), is("bob"));
        assertThat(rows.get(1).getInt("age"), is(25));
    }

    @Test
    public void shouldHandleMixedTypes() {
        String jsonl = "{\"id\":1,\"score\":3.14,\"active\":true}\n";

        JsonlSource source = new JsonlSource(new StringReader(jsonl));
        DataSet row = source.stream().findFirst().get();

        assertThat(row.getInt("id"), is(1));
        assertThat(row.getDouble("score"), is(3.14));
        assertThat(row.getBoolean("active"), is(true));
    }

    @Test
    public void shouldHandleNullValues() {
        String jsonl = "{\"name\":\"alice\",\"value\":null}\n";

        JsonlSource source = new JsonlSource(new StringReader(jsonl));
        DataSet row = source.stream().findFirst().get();

        assertThat(row.getString("name"), is("alice"));
        assertThat(row.isNull("value"), is(true));
    }

    @Test
    public void shouldReadJsonlFile() throws Exception {
        File file = new File(getClass().getClassLoader()
                .getResource("test.jsonl").toURI());
        JsonlSource source = new JsonlSource(file);
        List<DataSet> rows = source.stream().collect(Collectors.toList());

        assertThat(rows.size(), is(3));
        assertThat(rows.get(0).getString("name"), is("alice"));
        assertThat(rows.get(2).getString("name"), is("charlie"));
    }
}
