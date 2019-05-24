package net.kimleo.rec.core;

import org.junit.Test;

import java.util.Arrays;

import static net.kimleo.rec.core.Cell.cell;
import static net.kimleo.rec.core.Cell.empty;
import static net.kimleo.rec.core.Record.rec;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GettersTest {

    @Test
    public void shouldGetSuccessfully() {
        Getters getters = Getters.from(Arrays.asList("name", "age", "dob"));

        Getters.Getter getter = getters.of(rec("Kimmy", "100", "2010-10-10"));

        assertThat(getter.get("name"), is(cell("Kimmy")));
        assertThat(getter.get("age"), is(cell("100")));
        assertThat(getter.get("dob"), is(cell("2010-10-10")));
    }

    @Test
    public void shouldGetWithIndex() {
        Getters getters = Getters.NO_HEADER;

        Getters.Getter getter = getters.of(rec("hello"));

        assertThat(getter.get(0), is(cell("hello")));
    }

    @Test
    public void shouldGetEmptyWhenNoHeadersFound() {
        Getters getters = Getters.NO_HEADER;

        Getters.Getter getter = getters.of(rec("hello"));

        assertThat(getter.get("name"), is(Cell.empty()));
    }
}