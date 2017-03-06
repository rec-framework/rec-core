package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.sepval.parser.ParseConfig;
import net.kimleo.rec.v2.model.Target;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CSVFileSourceTest {
    @Test
    public void shouldSuccessfullyParseAStream() throws Exception {
        URL resource = this.getClass().getClassLoader().getResource("CSVFileSource.csv");
        assert resource != null;
        File file = new File(resource.toURI());
        CSVFileSource source = new CSVFileSource(file, "id, name, dob, illegal", ParseConfig.DEFAULT);

        CollectTee collector = new CollectTee(new ArrayList<>());
        HashSet<String> strings = new HashSet<>();

        ItemCounterTee counter = new ItemCounterTee(it -> true);

        Target target = record ->
                strings.add(String.format("%s - %s", record.get("id"), record.get("name")));
        source.tee(counter).to(target.tee(collector));

        assertThat(collector.collect().size(), is(counter.count()));

    }
}