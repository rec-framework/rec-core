package net.kimleo.rec.repository.selector.expr;

import net.kimleo.rec.repository.selector.AliasSelector;
import net.kimleo.rec.repository.selector.FieldSelector;
import net.kimleo.rec.repository.selector.Selector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class SelectorParser {

    private static final Map<String, SelectorTokenType> keywordsMap = new HashMap<>();
    private static final Map<Character, SelectorToken> specialCharsMap = new HashMap<>();


    static {
        keywordsMap.put("as", SelectorTokenType.AS);

        specialCharsMap.put('[', new SelectorToken(SelectorTokenType.LEFT_SQUARE));
        specialCharsMap.put(']', new SelectorToken(SelectorTokenType.RIGHT_SQUARE));
        specialCharsMap.put(',', new SelectorToken(SelectorTokenType.COMMA));
        specialCharsMap.put('.', new SelectorToken(SelectorTokenType.DOT));
    }

    public static List<SelectorToken> lex(String expr) {
        int index = 0;
        ArrayList<SelectorToken> tokens = new ArrayList<>();

        String symbol = "";

        while (index < expr.length()) {
            char current = expr.charAt(index);
            if (Character.isSpaceChar(current) && endWithKeywords(symbol.trim())) {
                tokens.addAll(makeTokenWithKeywords(symbol.trim()));
                symbol = "";
                index ++;
                continue;
            }
            if (specialCharsMap.containsKey(current)) {
                if (!symbol.trim().isEmpty())
                    tokens.add(makeToken(symbol.trim()));
                tokens.add(specialCharsMap.get(current));
                symbol = "";
                index ++;
                continue;
            }
            symbol += current;
            index ++;
        }

        if (!symbol.trim().isEmpty()) {
            tokens.add(new SelectorToken(SelectorTokenType.ID, symbol.trim()));
        }

        return tokens;
    }

    private static SelectorToken makeToken(String repr) {
        if (keywordsMap.containsKey(repr)) {
            return new SelectorToken(keywordsMap.get(repr));
        }
        return new SelectorToken(SelectorTokenType.ID, repr);
    }

    private static List<SelectorToken> makeTokenWithKeywords(String symbol) {
        SelectorToken keyword = null;

        SelectorToken actualSymbol = new SelectorToken(SelectorTokenType.ID, symbol);

        for (String key : keywordsMap.keySet()) {
            if (symbol.endsWith(key)) {
                keyword = new SelectorToken(keywordsMap.get(key));
                String subSymbol = symbol.substring(0, symbol.length() - key.length());
                if (subSymbol.trim().isEmpty()) {
                    return singletonList(keyword);
                }
                actualSymbol = new SelectorToken(SelectorTokenType.ID,
                        subSymbol);
                break;
            }
        }
        return (keyword == null) ? singletonList(actualSymbol) : asList(actualSymbol, keyword);
    }

    private static boolean endWithKeywords(String symbol) {
        return keywordsMap.keySet().stream().anyMatch(symbol::endsWith);
    }

    public static List<Selector> parse(List<SelectorToken> tokens) {
        ArrayList<Selector> selectors = new ArrayList<>();

        int index = 0;

        while (index < tokens.size()) {
            SelectorToken token = tokens.get(index);

            if (token.tokenType == SelectorTokenType.COMMA) {
                index ++;
                continue;
            }

            if (token.tokenType == SelectorTokenType.AS) {
                if (!selectors.isEmpty() && last(selectors) instanceof FieldSelector) {
                    index ++;
                    if (tokens.get(index).tokenType != SelectorTokenType.ID) {
                        throw new RuntimeException(String.format("Unexpected token %s: %s, should alias to an ID",
                                token.tokenType, token.repr));
                    }
                    FieldSelector last = (FieldSelector) last(selectors);
                    selectors.remove(selectors.size() - 1);
                    selectors.add(new AliasSelector(last, tokens.get(index).repr));
                } else {
                    throw new RuntimeException("Only an field selector can be aliased");
                }
                index ++;
                continue;
            }
            if (token.tokenType != SelectorTokenType.ID) {
                throw new RuntimeException(String.format("unexpected token config %s", token.tokenType));
            }

            index ++;
            if (index >= tokens.size()) {
                selectors.add(new FieldSelector(token.repr, singletonList("*")));
                break;
            }

            SelectorToken next = tokens.get(index);

            switch (next.tokenType) {
                case DOT: {
                    index ++;
                    SelectorToken property = tokens.get(index);

                    selectors.add(new FieldSelector(token.repr, singletonList(property.repr)));
                    break;
                }

                case COMMA: {
                    selectors.add(new FieldSelector(token.repr, singletonList("*")));
                    break;
                }

                case LEFT_SQUARE: {
                    ArrayList<String> properties = new ArrayList<>();
                    index ++;
                    while (tokens.get(index).tokenType != SelectorTokenType.RIGHT_SQUARE) {
                        SelectorToken property = tokens.get(index);
                        if (property.tokenType == SelectorTokenType.COMMA) {
                            index ++;
                            continue;
                        }
                        if (property.tokenType == SelectorTokenType.ID) {
                            properties.add(property.repr);
                        }
                        index ++;
                    }

                    selectors.add(new FieldSelector(token.repr, properties));
                    break;
                }
                default:
                    index++;
            }
            index ++;
        }
        return selectors;
    }

    private static Selector last(ArrayList<Selector> selectors) {
        return selectors.get(selectors.size() - 1);
    }
}
