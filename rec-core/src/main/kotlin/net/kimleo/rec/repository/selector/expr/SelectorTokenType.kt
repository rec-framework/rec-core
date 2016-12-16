package net.kimleo.rec.repository.selector.expr

enum class SelectorTokenType(val repr: String) {
    ID("ID"),
    DOT("."),
    LEFT_SQUARE("["),
    RIGHT_SQUARE("]"),
    COMMA(","),

    AS("as")
}