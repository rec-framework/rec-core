package net.kimleo.rec.repository.selector.expr

enum class SelectorTokenType(val repr: String) {
    ID("ID"),
    DOT("."),
    LEFT_SQUARE("["),
    RIGHT_SQUARE("]"),
    PIPE("|"),
    EQUAL("="),
    COMMA(","),
    IS("is"),
    MATCHES("matches"),
}