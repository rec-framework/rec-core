package net.kimleo.rec.repository.selector.expr

data class SelectorToken(val tokenType: SelectorTokenType, val repr: String = tokenType.repr) {

}