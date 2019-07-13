package net.kimleo.rec.parser;

import net.kimleo.rec.ast.Node;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class ParserTest {

    @Test
    public void test() {
        RecParser parser = parse("import hello;" +
                "stdin | stdout;");

        RecParser.StartContext start = parser.start();


        Node accept = start.accept(new RecAstVisitor());

        System.out.println(accept);

    }

    @Test
    public void testByFile() {
        RecParser parser = parseFile("basic.rec");

        RecParser.StartContext start = parser.start();


        Node accept = start.accept(new RecAstVisitor());

        System.out.println(accept);
    }

    private RecParser parse(String input) {
        ANTLRInputStream stream = new ANTLRInputStream(input);


        RecLexer recLexer = new RecLexer(stream);

        CommonTokenStream tokenStream = new CommonTokenStream(recLexer);


        return new RecParser(tokenStream);
    }

    private RecParser parseFile(String resource) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resource);

        try {
            return new RecParser(new CommonTokenStream(new RecLexer(new ANTLRInputStream(inputStream))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
