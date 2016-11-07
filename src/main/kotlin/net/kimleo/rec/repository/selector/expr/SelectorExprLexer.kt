package net.kimleo.rec.repository.selector.expr

import net.kimleo.rec.bind
import net.kimleo.rec.orElse

class SelectorExprLexer {

    val keywordsMap: Map<String, SelectorTokenType> = mapOf<String, SelectorTokenType>(
            Pair("is", SelectorTokenType.IS),
            Pair("matches", SelectorTokenType.MATCHES)
    )
    
    fun lex(expr: String): List<SelectorToken> {
        val specialCharsMap = mapOf<Char, SelectorToken>(
                Pair('[', SelectorToken(SelectorTokenType.LEFT_SQUARE)),
                Pair(']', SelectorToken(SelectorTokenType.RIGHT_SQUARE)),
                Pair('|', SelectorToken(SelectorTokenType.PIPE)),
                Pair(',', SelectorToken(SelectorTokenType.COMMA)),
                Pair('.', SelectorToken(SelectorTokenType.DOT)),
                Pair('=', SelectorToken(SelectorTokenType.EQUAL))
        )
        var index = 0
        val tokens = arrayListOf<SelectorToken>()

        var symbol = ""
        while (index < expr.length) {
            val current = expr[index]
            if (Character.isSpaceChar(current) && endWithKeywords(symbol.trim())) {
                tokens.addAll(makeTokenWithKeywords(symbol.trim()))
                symbol = ""
                index++
                continue
            }
            if (current in specialCharsMap) {
                tokens.add(makeToken(symbol.trim()))
                tokens.add(specialCharsMap[current]!!)
                symbol = ""
                index++
                continue
            }
            symbol += current
            index++
        }
        if (!symbol.isNullOrEmpty()) tokens.add(SelectorToken(SelectorTokenType.ID, symbol.trim()))

        return tokens
    }

    private fun makeTokenWithKeywords(symbol: String): List<SelectorToken> {
        var keyword: SelectorToken? = null
        var actualSymbol = SelectorToken(SelectorTokenType.ID, symbol)
        for (key in keywordsMap.keys) {
            if (symbol.endsWith(key)) {
                keyword = SelectorToken(keywordsMap[key]!!)
                actualSymbol = SelectorToken(SelectorTokenType.ID, symbol.dropLast(key.length))
                break
            }
        }

        return keyword.bind { listOf(actualSymbol, it) }.orElse { listOf(actualSymbol) }
    }

    private fun endWithKeywords(symbol: String): Boolean {
        for (keyword in keywordsMap.keys) {
            if (symbol.endsWith(keyword))
                return true
        }
        return false
    }

    private fun makeToken(symbol: String): SelectorToken {
        if (symbol in keywordsMap) {
            return SelectorToken(keywordsMap[symbol]!!)
        } else {
            return SelectorToken(SelectorTokenType.ID, symbol)
        }
    }

}