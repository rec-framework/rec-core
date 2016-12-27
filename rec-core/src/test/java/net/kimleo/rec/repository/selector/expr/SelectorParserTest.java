package net.kimleo.rec.repository.selector.expr;

import net.kimleo.rec.repository.selector.AliasSelector;
import net.kimleo.rec.repository.selector.FieldSelector;
import net.kimleo.rec.repository.selector.Selector;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static net.kimleo.rec.repository.selector.expr.SelectorParser.*;
import static org.junit.Assert.*;

public class SelectorParserTest {

    @Test
    public void testParser() {
        List<SelectorToken> lex = lex("cust, prod.hello world, i2b[name, pass], intj");
        List<Selector> parse = parse(lex);

        assertTrue(parse.size() == 4);
        assertTrue(((FieldSelector) parse.get(0)).properties.size() == 1);
        assertTrue(Objects.equals(((FieldSelector) parse.get(1)).properties.get(0), "hello world"));
        assertTrue(((FieldSelector) parse.get(2)).properties.size() == 2);
    }

    @Test
    public void testAlias() {
        List<SelectorToken> lex = lex("cust[name, age] as person");
        List<Selector> parse = parse(lex);

        assertTrue(parse.size() == 1);

        assertEquals(((AliasSelector) parse.get(0)).alias, "person");
    }

}