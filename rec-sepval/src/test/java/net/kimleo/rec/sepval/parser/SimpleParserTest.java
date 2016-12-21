package net.kimleo.rec.sepval.parser;

import net.kimleo.rec.sepval.SepValEntry;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SimpleParserTest {


    private final SimpleParser parser = new SimpleParser();

    @Test
    public void shouldParseSimpleCSV() throws Exception {
        SepValEntry tuple = parser.parse("a, b, c");

        assertEquals(tuple.getSize(), 3);
        assertEquals(tuple.get(1), "b");

    }

    @Test
    public void shouldParseQuotedString() throws Exception {
        SepValEntry tuple = parser.parse("\"abc\",    \" \"\" \t\r\n\\\b def \"    , g");

        assertEquals(tuple.getSize(), 3);
        assertEquals(tuple.get(1), " \" \t\r\n\\\b def ");
        assertEquals(tuple.get(2), "g");

    }

    @Test
    public void shouldParseDifferentDelimiter() throws Exception {
        SepValEntry tuple = new SimpleParser(new ParseConfig('|'))
                .parse("hello|world|    123456|\"\"\"I have a dream\"\"\"");

        assertEquals(tuple.getSize(), 4);
        assertEquals(tuple.get(0), "hello");
        assertEquals(tuple.get(2), "123456");
        assertEquals(tuple.get(3), "\"I have a dream\"");
    }

    @Test
    public void shouldParseRecordFormat() throws Exception {
        SepValEntry tuple = parser.parse("first name, last name, {2}, ID, ..., comment");

        assertNotNull(tuple);

    }
}