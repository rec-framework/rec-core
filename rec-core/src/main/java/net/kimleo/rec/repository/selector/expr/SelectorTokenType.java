package net.kimleo.rec.repository.selector.expr;

public enum SelectorTokenType {
    ID("ID"),
    DOT("."),

    LEFT_SQUARE("]"),
    RIGHT_SQUARE("]"),
    COMMA(","),

    AS("as");

    String repr;

    SelectorTokenType(String repr) {
        this.repr = repr;
    }
}
