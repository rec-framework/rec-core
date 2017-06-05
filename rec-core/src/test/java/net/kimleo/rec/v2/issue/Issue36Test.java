package net.kimleo.rec.v2.issue;


import net.kimleo.rec.concept.Accessible;
import net.kimleo.rec.concept.Mapped;
import net.kimleo.rec.sepval.SepValEntry;
import net.kimleo.rec.sepval.parser.SimpleParser;
import net.kimleo.rec.v2.accessor.Accessor;
import net.kimleo.rec.v2.accessor.RecordWrapper;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

// See https://github.com/rec-framework/rec-core/issues/36
public class Issue36Test {

    private final SimpleParser parser = new SimpleParser();

    @Test
    public void shouldOriginallyPass() throws Exception {

        SepValEntry entry = parser.parse("1, 2, 3, 4, 5,,,,123");


        Accessor<String> accessor = new Accessor<>(new String[] {"1", "{3}", "5", "...", "last"});
        RecordWrapper<String> wrapper = accessor.of(entry);
        assertThat(wrapper.get("last"), is("123"));
    }

    @Test
    public void shouldPassWhenSpecifyAllFields() throws Exception {
        SepValEntry entry = parser.parse("1, 2, 3, 4, 5,,,,");

        Accessor<String> accessor = new Accessor<>(new String[] {"1", "{3}", "5", "{3}", "last"});
        RecordWrapper<String> wrapper = accessor.of(entry);

        // Previously: AssertionError || IndexOutOfBoundException
        assertThat(wrapper.get("last") , is(""));
    }
}
