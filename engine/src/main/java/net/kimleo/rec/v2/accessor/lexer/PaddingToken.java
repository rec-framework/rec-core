package net.kimleo.rec.v2.accessor.lexer;

public class PaddingToken implements FieldType {
    private final int size;

    public PaddingToken(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaddingToken padding = (PaddingToken) o;

        return size == padding.size;
    }

    @Override
    public int hashCode() {
        return size;
    }

    @Override
    public String toString() {
        return String.format("{%d}", size);
    }
}
