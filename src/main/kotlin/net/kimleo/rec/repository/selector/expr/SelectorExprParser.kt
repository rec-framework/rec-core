package net.kimleo.rec.repository.selector.expr

import com.sun.xml.internal.bind.v2.model.core.ID
import net.kimleo.rec.repository.selector.FieldSelector
import net.kimleo.rec.repository.selector.Selector

class SelectorExprParser {

    fun parse(tokens: List<SelectorToken>): List<Selector> {
        var selectors = arrayListOf<Selector>()

        var index = 0

        while (index < tokens.size) {
            val type = tokens[index]

            if (type.tokenType == SelectorTokenType.COMMA) {
                index ++
                continue
            }

            if (type.tokenType != SelectorTokenType.ID) {
                throw RuntimeException("unexpected token type ${type.tokenType}")
            }

            index ++

            if (index >= tokens.size) {
                selectors.add(FieldSelector(type.repr, listOf("*")))
                break
            }

            val next = tokens[index]

            when (next.tokenType) {
                SelectorTokenType.DOT -> {
                    index ++
                    val property = tokens[index]

                    selectors.add(FieldSelector(type.repr, listOf(property.repr)))
                }
                SelectorTokenType.COMMA -> {
                    selectors.add(FieldSelector(type.repr, listOf("*")))
                }
                SelectorTokenType.LEFT_SQUARE -> {
                    val properties = arrayListOf<String>()
                    index ++
                    while (tokens[index].tokenType != SelectorTokenType.RIGHT_SQUARE) {
                        val property = tokens[index]
                        if (property.tokenType == SelectorTokenType.COMMA) {
                            index ++
                            continue
                        }
                        if (property.tokenType == SelectorTokenType.ID)  {
                            properties.add(property.repr)
                        }
                        index ++
                    }

                    selectors.add(FieldSelector(type.repr, properties))
                    index ++
                }

                else -> {
                    index++
                }
            }
            index ++
        }

        return selectors
    }
}