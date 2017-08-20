package net.kimleo.rec.v2.accessor.lexer;

public class FieldNameToken implements FieldType {
    private final String name;

    FieldNameToken(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldNameToken fieldName = (FieldNameToken) o;

        return name != null ? name.equals(fieldName.name) : fieldName.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
