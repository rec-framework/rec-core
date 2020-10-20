package net.kimleo.rec.parser;

import net.kimleo.rec.ast.Node;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

import java.io.InputStream;

public class ParserTest {

    @Test
    public void test() {
        RecPipeParser parser = parse("import hello;" +
                "stdin | stdout;");

        RecPipeParser.StartContext start = parser.start();


        Node accept = start.accept(new RecPipeAstVisitor());

        System.out.println(accept);

    }

    @Test
    public void testByFile() {
        RecPipeParser parser = parseFile("basic.rec");

        RecPipeParser.StartContext start = parser.start();

        Node accept = start.accept(new RecPipeAstVisitor());

        System.out.println(accept);
    }

    private RecPipeParser parse(String input) {
        CharStream stream = CharStreams.fromString(input);


        RecPipeLexer recLexer = new RecPipeLexer(stream);

        CommonTokenStream tokenStream = new CommonTokenStream(recLexer);


        return new RecPipeParser(tokenStream);
    }

    private RecPipeParser parseFile(String resource) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resource);

        try {
            return new RecPipeParser(new CommonTokenStream(new RecPipeLexer(CharStreams.fromStream(inputStream))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
