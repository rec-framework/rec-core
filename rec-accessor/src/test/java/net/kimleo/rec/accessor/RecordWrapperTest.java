package net.kimleo.rec.accessor;

import net.kimleo.rec.Pair;
import net.kimleo.rec.record.Record;
import net.kimleo.rec.sepval.parser.ParseConfig;
import org.junit.Test;

import java.util.Map;

import static net.kimleo.rec.API.accessor;
import static net.kimleo.rec.API.rec;
import static net.kimleo.rec.accessor.lexer.Lexer.buildFieldMapPair;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RecordWrapperTest {
    @Test
    public void shouldBuildMapPairSuccessfully() throws Exception {
        Pair<Map<String, Integer>, Integer> pair = buildFieldMapPair("name", "age", "...", "email", "{1}", "comment");

        Map<String, Integer> map1 = pair.getFirst();

        assertTrue(map1.get("name") == 0);
        assertTrue(map1.get("comment") == -1);
        assertTrue(map1.get("email") == -3);

        assertTrue(pair.getSecond() == 3);

        Pair<Map<String, Integer>, Integer> pair2 = buildFieldMapPair("hello", "{3}", "world", "...", "is", "{12}", "beep", "{6}", "end");
        Map<String, Integer> map2 = pair2.getFirst();

        assertTrue(map2.get("hello") == 0);
        assertTrue(map2.get("world") == 4);
        assertTrue(map2.get("is") == -21);
        assertTrue(pair2.getSecond() == 21);
    }

    @Test
    public void shoudAccessCorrectRecord() throws Exception {
        Record rec = rec("Kimmy, Leo, 10, male, 1993/07/09");

        Accessor<String> acc = accessor(rec("first name, {1}, age, ..., dob"));
        RecordWrapper<String> kimmy = acc.of(rec);

        assertEquals(kimmy.get("first name"), "Kimmy");

        assertEquals(kimmy.get("age"), "10");
        assertEquals(kimmy.get("dob"), "1993/07/09");
    }

    @Test
    public void shouldAccessSpaceSeperatedRecord() throws Exception {
        Record record = rec("[INFO] 2015-02-10 12:35:20PM+8:00 net.kimleo.net.kimleo.rec.Application \"hello world\"",
                new ParseConfig(' ', '"'));

        Accessor<String> acc = accessor(rec("level, date, ..., message"));
        RecordWrapper<String> kimmy = acc.of(record);

        assertEquals(kimmy.get("level"), "[INFO]");
        assertEquals(kimmy.get("message"), "hello world");

    }
}