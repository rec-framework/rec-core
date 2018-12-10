package net.kimleo.rec.dsl.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.kimleo.rec.util.CollectUtils.between;

public class Lexer {

    class LexState {
        final String source;
        private int index = 0;

        LexState(String source) {
            this.source = source;
        }

        boolean eof() {
            return index >= source.length();
        }

        char current() {
            return this.source.charAt(index);
        }

        LexState next() {
            this.index++;
            return this;
        }
    }


    private final Set<String> KEYWORDS = Arrays.stream(TokenType.values())
            .filter(between(TokenType.UNKNOWN, TokenType.IDENTIFIER))
            .map(TokenType::getRepr).collect(Collectors.toSet());

    List<Token> tokenize(String source) {
        return tokenize(new LexState(source));
    }

    private List<Token> tokenize(LexState lexState) {
        return null;
    }

}
