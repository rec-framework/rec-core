package net.kimleo.rec.repository.selector.expr

import net.kimleo.rec.bind
import net.kimleo.rec.orElse
import net.kimleo.rec.repository.selector.CombinedSelector
import net.kimleo.rec.repository.selector.Selector

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