package net.kimleo.rec.dsl.parser;

import lombok.Getter;

@Getter
public enum TokenType {
    UNKNOWN,
    STREAM("stream"),

    SOURCE("source"),
    TARGET("target"),

    PROCESSOR("processor"),

    SKIP("skip"),
    EMIT("emit"),

    TRUE("true"),
    FALSE("false"),
    NIL("nil"),

    STDIN("stdin"),
    STDOUT("stdout"),
    STDERR("stderr"),

    IF("if"),
    ELSE("else"),

    MATCH("match"),
    CASE("case"),

    IMPORT("import"),

    IDENTIFIER,

    NUM_VALUE,
    STRING_VALUE,

    SYMBOLS,

    PIPE("|"),
    LPAREN("("), RPAREN(")"),
    LSQUARE("["), RSQUARE("]"),
    LCURLY("{"), RCURLY("}"),
    LANGLE("<"), RANGLE(">"),
    COLUMN(":"),

    COMMA(","),

    QUESTION_MARK("?"),
    EXAMINATION("!"),
    ASTERISK("*"),
    DOLLAR("$"),
    ;

    String repr = null;
    TokenType() {
    }

    TokenType(String repr) {
        this.repr = repr;
    }
}
