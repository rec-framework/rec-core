package net.kimleo.rec.repository.selector;

import net.kimleo.rec.repository.selector.expr.SelectorParser;
import net.kimleo.rec.repository.selector.expr.SelectorToken;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SelectorTest {

    @Test
    public void testLexer() {
        List<SelectorToken> result = SelectorParser.lex("hello hello, hello.name, hello world[hahaha] as fuck, hello");
        result.forEach(System.out::println);
    }

}