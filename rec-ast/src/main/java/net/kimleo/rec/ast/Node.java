package net.kimleo.rec.ast;

public interface Node {

    default String repr() {
        return String.format("<node %d>", hashCode());
    }
}
