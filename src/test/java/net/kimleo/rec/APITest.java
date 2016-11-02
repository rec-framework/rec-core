package net.kimleo.rec;

import net.kimleo.rec.accessor.Accessor;
import net.kimleo.rec.accessor.AccessorFactory;
import net.kimleo.rec.record.Record;
import org.junit.Test;

import static net.kimleo.rec.API.accessor;
import static net.kimleo.rec.API.rec;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class APITest {
    @Test
    public void testRecord() throws Exception {
        Record record = rec("Kimmy, Leo, male, 10, 1999/99/99");

        AccessorFactory accessor = accessor(rec("first name, ..., gender, {1}, dob"));

        Accessor kimmy = accessor.of(record);

        assertThat(kimmy.get("first name"), is("Kimmy"));
        assertThat(kimmy.get("gender"), is("male"));
        assertThat(kimmy.get("dob"), is("1999/99/99"));

    }
}
