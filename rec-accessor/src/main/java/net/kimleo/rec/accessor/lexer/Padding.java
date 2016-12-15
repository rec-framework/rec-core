package net.kimleo.rec.accessor.lexer;

public class Padding implements FieldType {
    private final int size;

    public Padding(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Padding padding = (Padding) o;

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
