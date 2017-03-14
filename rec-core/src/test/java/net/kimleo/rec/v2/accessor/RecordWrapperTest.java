package net.kimleo.rec.v2.accessor;

import net.kimleo.rec.Pair;
import org.junit.Test;

import java.util.Map;

import static net.kimleo.rec.v2.accessor.lexer.Lexer.buildFieldMapPair;
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
}