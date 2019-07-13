package net.kimleo.rec.ast;

import java.util.Arrays;

public enum Relation implements Node {
    GREATER_THAN(">"),
    LESS_THAN("<"),
    EQUAL("="),
    GREATER_EQUAL(">="),
    LESS_EQUAL("<="),
    IN("in"),
    NOT_EQUAL("!=");


    public final String repr;

    Relation(String repr) {
        this.repr = repr;
    }


    public static Relation of(String repr) {
        return Arrays.stream(values())
                .filter(rel -> rel.repr.equals(repr))
                .findAny().orElse(null);
    }
}
