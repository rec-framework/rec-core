package net.kimleo.rec.repository.selector.expr;

final public class SelectorToken {

    public final SelectorTokenType tokenType;
    public final String repr;

    public SelectorToken(SelectorTokenType tokenType, String repr) {
        this.tokenType = tokenType;
        this.repr = repr;
    }

    public SelectorToken(SelectorTokenType tokenType) {
        this(tokenType, tokenType.repr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectorToken that = (SelectorToken) o;

        if (tokenType != that.tokenType) return false;
        return repr != null ? repr.equals(that.repr) : that.repr == null;
    }

    @Override
    public int hashCode() {
        int result = tokenType != null ? tokenType.hashCode() : 0;
        result = 31 * result + (repr != null ? repr.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", tokenType.name(), repr);
    }
}
