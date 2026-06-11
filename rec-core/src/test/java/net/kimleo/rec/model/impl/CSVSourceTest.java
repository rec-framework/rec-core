package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.sepval.parser.ParseConfig;
import net.kimleo.rec.model.Target;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CSVSourceTest {
    @Test
    public void shouldSuccessfullyParseAStream() throws Exception {
        URL resource = this.getClass().getClassLoader().getResource("CSVFileSource.csv");
        assert resource != null;
        File file = new File(resource.toURI());
        CSVSource source = new CSVSource(Files.newBufferedReader(file.toPath()),
                "id, name, dob, illegal", ParseConfig.DEFAULT);

        CollectTee collector = new CollectTee();
        HashSet<String> strings = new HashSet<>();

        ItemCounterTee counter = new ItemCounterTee(it -> true);

        Target target = record ->
                strings.add(String.format("%s - %s", record.getString("id"), record.getString("name")));
        source.tee(counter).to(target.tee(collector));

        assertThat(collector.collect().size(), is(counter.getCount()));

    }
}