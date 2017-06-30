package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.concept.Mapped;
import net.kimleo.rec.sepval.parser.ParseConfig;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BufferedCachingTeeTest {
    private int sum = 0;

    @Test
    public void shouldCachingAsExpected() throws Exception {
        URL resource = this.getClass().getClassLoader().getResource("CSVFileSource.csv");
        assert resource != null;
        File file = new File(resource.toURI());
        CSVSource source = new CSVSource(Files.newBufferedReader(file.toPath()),
                "question, answer, duration", ParseConfig.DEFAULT);

        BufferedCachingTee catching = new BufferedCachingTee(100 * 1024 * 1024);
        source.tee(catching).to(record -> sum(record.get("question")));

        catching.source().to(record -> sum(record.get("answer")));

        assertThat(sum, is(1333248)); // 8 * 166656

        ItemCounterTee<Mapped<String>> counter = new ItemCounterTee<>(it -> true);

        // Re-enterrable
        catching.source().tee(counter).to((record) -> {});

        assertThat(counter.getCount(), is(166656));
    }

    private void sum(String str) {
        sum += str.length();
    }


}